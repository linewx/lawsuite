package com.linewx.law.instrument;

import com.google.gson.Gson;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.parser.InstrumentParser;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.ParseStateMachine;
import com.linewx.law.parser.json.RuleJson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luganlin on 11/22/16.
 */
public class MainApp {
    private static Logger logger = LoggerFactory.getLogger(MainApp.class);
    private static AtomicLong total = new AtomicLong(0L);
    private static AtomicLong error = new AtomicLong(0L);
    private static AtomicLong unsupported = new AtomicLong(0L);

    public static void main(String argv[]) throws Exception {
        RuleJson rule = new MainApp().readRule();
        //testRe();

        parseFiles(rule, "C:\\Users\\lugan\\git\\law\\sourcefile");
        //parseFilesSync(rule, "/users/luganlin/Documents/download");
        //parseFile(rule, "C:\\Users\\lugan\\git\\law\\sourcefile\\e68353f8-f8c1-4e29-99b4-a762cb858dc5.html");

    }

    public static void parseFile(RuleJson rule, String fileName) throws Exception {
        File file = new File(fileName);
        List<String> statements = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(file, "GBK");
            Element element = doc.getElementById("DivContent");
            Elements elements = element.children();

            for (Element oneElement : elements) {
                statements.add(oneElement.ownText());
            }

            InstrumentParser parser = new InstrumentParser(rule, InstrumentTypeEnum.CIVIL_JUDGMENT);
            parser.parse(statements);
        }catch(Exception e) {
            logger.error(String.join("\n", statements) + "\n" + file.getName(), e);
        }

    }


    public static void parseFiles(RuleJson rule, String folder) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(8);
        File dir = new File(folder);

        List<Future> futures = new ArrayList<>();
        for (File file : dir.listFiles()) {
            Future<Boolean> future = executor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    ParseContext context = new ParseContext();
                    List<String> statements = new ArrayList<>();
                    try {
                        Document doc = Jsoup.parse(file, "GBK");
                        Element element = doc.getElementById("DivContent");
                        Elements elements = element.children();


                        context.addResult("filename", file.getName());
                        for (Element oneElement : elements) {
                            statements.add(oneElement.ownText());
                        }

                        InstrumentParser parser = new InstrumentParser(rule, InstrumentTypeEnum.CIVIL_JUDGMENT);
                        parser.parse(statements);
                        total.incrementAndGet();

                    } catch (InstrumentParserException e) {
                        if (!e.getErrorCode().equals(InstrumentParserException.ErrorCode.UNSUPPORTED_TYPE)) {
                            logger.error(String.join("\n", statements) + "\n" + file.getName(), e);
                            total.incrementAndGet();
                            error.incrementAndGet();
                            /*System.out.println("*********** validation error ****************");
                            System.out.println("-- origin content --");
                            //System.out.println("file name: " + file.getName());
                            System.out.println(String.join("\n", statements));
                            System.out.println("-- error message --");
                            System.out.println("file name:" + file.getName());
                            System.out.println(e.getMessage());*/
                            //System.out.println("*********** end validation error ************");
                        }else {
                            total.incrementAndGet();
                            unsupported.incrementAndGet();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        total.incrementAndGet();
                        error.incrementAndGet();
                    }
                    return true;
                }
            });

            futures.add(future);
        }

        for (Future future : futures) {
            future.get();
        }

        System.out.println("total:" + total + "," + "unsupport: " + unsupported + "," + "error:" + error);
        System.out.println("民事判决书识别率:" + (total.longValue()-unsupported.longValue()-error.longValue())*100/(total.longValue()-unsupported.longValue()) + "%");
        long endTime = System.currentTimeMillis();
        executor.shutdown();
    }


    public RuleJson readRule() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("rule.json");
        Gson gson = new Gson();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(is));
        RuleJson rule = gson.fromJson(bufferedReader, RuleJson.class);
        return rule;
    }

    /*public static void loadReason() throws IOException {
        Map<String, String> reasons = new HashMap<>();
        Map<String, String> reasonIndex = new HashMap<>();
        Map<String, String> secondaryIndex = new HashMap<>();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("reason.csv");
        Gson gson = new Gson();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(is));
        String strLine;
        while ((strLine = bufferedReader.readLine()) != null)   {
            // Print the content on the console
            String[] oneReason = strLine.split(",");
            String number = oneReason[0].trim();
            String name = oneReason[1].trim();

            reasons.put(number, name);
            reasonIndex.put(name, number);

            for(int i=0; i<name.length(); i++) {
                if (name.charAt(i) == '、') {
                    secondaryIndex.put(name.substring(i+1), number);
                }
            }
        }


    }
*/
    static void testRe() {
     /*   Pattern pattern = Pattern.compile(".*(?<!日|二)$");

        Matcher matcher = pattern.matcher("审判员日");
        System.out.println(matcher.matches());


        //match prefix
        //（2015）宁民申177号, （2015）宁民申字第177号，匹配申
        Pattern prefixPattern = Pattern.compile(".*([^字|第\\d]).*号$");
        Matcher prefixMatcher = prefixPattern.matcher("原告何建与被告易思博南京分公司劳动争议纠纷");
        if (prefixMatcher.find()) {
            System.out.println(prefixMatcher.group(1));
        }

        Pattern testPattern = Pattern.compile("(合同|同)aa$");
        Matcher testMatcher = testPattern.matcher("商业合同aa");
        if (testMatcher.find()) {
            System.out.println(testMatcher.group(1));
        }*/

        //Pattern amountPattern = Pattern.compile(".*?(产品质量损害赔偿纠纷|盗窃、抢夺武装部队公文、证件、印章罪|追偿权纠纷|高利转贷罪|过失损坏交通设施罪|名誉权纠纷|盗窃、抢夺、毁灭国家机关公文、证件、印章罪|侵害植物新品种权纠纷|固体废物污染责任纠纷|试用买卖合同纠纷|国债回购合同纠纷|农机作业服务合同纠纷|供用气合同纠纷|虐待部属罪|船舶经营管理合同纠纷|武器装备肇事罪|宅基地使用权纠纷|违法向关系人发放贷款罪|漂流物返还纠纷|危险驾驶罪|职务发明创造发明人、设计人奖励、报酬纠纷|票据返还请求权纠纷|供用电合同纠纷|建设工程监理合同纠纷|保安服务合同纠纷|申请执行涉外仲裁裁决|专利合同纠纷|虐待俘虏罪|不解救被拐卖、绑架妇女、儿童罪|证券纠纷|公共道路妨碍通行损害责任纠纷|追收非正常收入纠纷|金融不良债权追偿纠纷|出版者权权属纠纷|企业出售合同纠纷|假冒专利罪|组织、利用会道门、邪教组织、利用迷信破坏法律实施罪|侵害集体经济组织成员权益纠纷|企业分立合同纠纷|责任保险合同纠纷|进出口信用保险合同纠纷|建筑物、搁置物、悬挂物塌落损害赔偿纠纷|保证保险合同纠纷|占有保护纠纷|徇私舞弊造成破产、亏损罪|申请认可和执行台湾地区仲裁裁决|与企业有关的纠纷|侵害经营秘密纠纷|网络域名权属纠纷|职务侵占罪|网络购物合同纠纷|演出合同纠纷|战时拒不救治伤病军人罪|金融工作人员购买假币、以假币换取货币罪|监护权特别程序案件|工伤保险待遇纠纷|经贸|人身保险合同纠纷|伪造、变造股票、公司、企业债券罪|窃取、收买、非法提供信用卡信息罪|典当纠纷|著作权权属、侵权纠纷|雇用逃离部队军人罪|代位继承纠纷|暴动越狱罪|申请宣告公民死亡|商品房预约合同纠纷|水路旅客运输合同纠纷|铁路旅客运输合同纠纷|餐饮服务合同纠纷|非法出租、出借枪支罪|聚众哄抢罪|信用卡纠纷|著作权合同纠纷|拐卖人口罪|捆绑销售不正当竞争纠纷|诬告陷害罪|留置权纠纷|非法经营同类营业罪|开设赌场罪|海运集装箱租赁合同纠纷|网络域名许可使用合同纠纷|婚姻自主权纠纷|重大环境污染事故罪|产品销售者责任纠纷|对违法票据承兑、付款、保证罪|逃税罪|技术培训合同纠纷|过失致人重伤罪|共有权确认纠纷|企业承包经营合同纠纷|强制猥亵、侮辱妇女罪|建筑物和其他土地附着物抵押权纠纷|组织他人偷越国（边）境罪|畜牧|生产、销售不符合卫生标准的化妆品罪|医疗服务合同纠纷|捆绑交易纠纷|广播电视电影|侮辱国旗、国徽罪|出售、购买、运输假币罪|引诱、教唆、欺骗他人吸毒罪|入境发展黑社会组织罪|反革命罪|表演合同纠纷|虚开增值税专用发票、用于骗取出口退税、抵押税款发票罪|一般人格权纠纷|家政服务合同纠纷|行政强制|车位纠纷|公司债券交易纠纷|二审无罪赔偿|金融不良债权转让合同纠纷|串通投标罪|擅离、玩忽军事职守罪|组织、利用会道门、邪教组织利用迷信致人死亡罪|证券内幕交易责任纠纷|联营合同纠纷|聚众扰乱军事管理区秩序罪|最高额质权纠纷|嫖宿幼女罪|占有物返还纠纷|失踪人债务支付纠纷|破坏集体生产罪|非法搜查罪|破坏永久性测量标志罪|统计|传授犯罪方法罪|证券权利确认纠纷|申请认可和执行澳门特别行政区法院民事判决|饲养动物致人损害赔偿纠纷|申请海事支付令|煽动分裂国家罪|技术中介合同纠纷|贪污贿赂罪|殡葬服务合同纠纷|信用证欺诈纠纷|背信损害上市公司利益罪|擅自改变武器装备编配用途罪|行政命令|聚众斗殴罪|人格权纠纷|紧急避险损害赔偿纠纷|技术进口合同纠纷|共有物分割纠纷|港口作业纠纷|申请撤销监护人资格|申请认可和执行香港特别行政区法院民事判决|破坏广播电视设施、公用电信设施罪|非法行医罪|放纵走私罪|船舶检验合同纠纷|阻碍军人执行职务罪|妨害动植物防疫、检疫罪|不正当竞争纠纷|义务帮工人受害赔偿、补偿纠纷|追收未缴出资纠纷|虐待罪|伪造、冒用产品质量标志纠纷|土地承包经营权确认纠纷|组织未成年人进行违反治安管理活动罪|组织、领导、参加恐怖组织罪|妨害信用卡管理罪|夫妻财产约定纠纷|合伙企业纠纷|不当得利纠纷|仿冒纠纷|传染病防治失职罪|拒不救援友邻部队罪|所有权确认纠纷|伪造货币或贩运伪造的货币罪|船舶改建合同纠纷|申请宣告公民失踪|植物新品种权权属纠纷|因申请诉前停止侵害著作权损害责任纠纷|产品运输者责任纠纷|离婚纠纷|其他合同、无因管理、不当得利纠纷|机动车交通事故责任纠纷|聚众冲击国家机关罪|擅自使用他人企业名称、姓名纠纷|违令作战消极罪|重婚罪|侵犯公民人身权利、民主权利罪|集体合同纠纷|危害公共卫生罪|产品责任纠纷|确认合同效力纠纷|探矿权纠纷|扰乱市场秩序罪|故意损毁名胜古迹罪|修理、重作、更换纠纷|行政规划|财会服务合同纠纷|掠夺定价纠纷|最高额抵押权纠纷|水污染责任纠纷|金融委托理财合同纠纷|申请拍卖扣押船载货物|违规制造、销售枪支罪|票据付款请求权纠纷|执行分配方案异议之诉|业主共有权纠纷|破坏交通工具罪|危害国防利益罪|侵犯通信自由罪|复制合同纠纷|申请认可和执行澳门特别行政区仲裁裁决|其他行政行为|滥用市场支配地位纠纷|遗失、抛弃高度危险物损害责任纠纷|旅游|物权保护纠纷|非法进行节育手术罪|中介组织人员提供虚假证明文件罪|扰乱公共秩序罪|多式联运合同纠纷|期货强行平仓纠纷|票据纠纷|行政确认|知识产权|违法司法拘留赔偿|伪造、变造、买卖武装部队公文、证件、印章罪|建筑物区分所有权纠纷|采集、供应血液、制作、供应血液制品事故罪|确认不侵害商标权纠纷|扰乱法庭秩序罪|走私制毒物品罪|认定公民无民事行为能力、限制民事行为能力案件|劫持船只、汽车罪|银行结算合同纠纷|海上、通海水域养殖损害责任纠纷|过失致人死亡罪|假冒他人专利纠纷|普通合伙纠纷|盐业|行政裁决|保证合同纠纷|伪证罪|伪造、变造、转让金融机构经营许可证、批准文件罪|股票回购合同纠纷|脱逃罪|为他人提供书号出版淫秽书刊罪|颠覆国家政权罪|破坏界碑、界桩罪|多式联运合同纠纷（由海事法院受理的）|商检徇私舞弊罪|荣誉权纠纷|妨害国（边）境管理罪|虚开发票罪|应收账款质权纠纷|娱乐服务合同纠纷|展览合同纠纷|伪造、变造国家有价证券罪|损害股东利益责任纠纷|铁路|航空运输损害责任纠纷|侵害网络域名纠纷|非法转让、倒卖土地使用权罪|隐瞒、谎报军情罪|专利申请权权属纠纷|中外合作勘探开发自然资源合同纠纷|经营者集中纠纷|盗掘古文化遗址、古墓葬罪|民用航空运输销售代理合同纠纷|草原|聚众淫乱罪|请求变更人民调解协议纠纷|中外合作经营企业合同纠纷|国家机关工作人员签订、履行合同失职罪|战时造谣惑众罪|引诱、容留、介绍卖淫罪|非法制造、出售非法制造的用于骗取出口退税、抵押税款发票罪|业主知情权纠纷|林业|合伙协议纠纷|海上、通海水域货物运输合同纠纷|破产抵销权纠纷|票据利益返还请求权纠纷|有价证券诈骗罪|资助危害国家安全犯罪活动罪|船舶属具保管合同纠纷|破坏军婚罪|商品房委托代理销售合同纠纷|进出口押汇纠纷|计划生育|环境监管失职罪|港口货物保管合同纠纷|非法收购盗伐、滥伐的林木罪|合同诈骗罪|劫夺被押解人员罪|小额借款合同纠纷|民事信托纠纷|养殖权纠纷|雇主损害赔偿纠纷|徇私舞弊罪|医疗保险待遇纠纷|劳动和社会保障|分家析产纠纷|建设工程分包合同纠纷|法定继承纠纷|侵害作品翻译权纠纷|行政协助|因申请诉前停止侵害注册商标专用权损害责任纠纷|申请诉前停止侵害植物新品种权|侵害技术秘密纠纷|非法出售、私赠文物藏品罪|水上运输财产损害责任纠纷|民间借贷纠纷|侵犯少数民族风俗习惯罪|物价|人寿保险合同纠纷|申请撤销宣告失踪|办理偷越国（边）境人员出入境证件罪|寻衅滋事罪|破产债权确认纠纷|消防|战时拒绝、故意延误军事订货罪|建设工程设计合同纠纷|建筑设备租赁合同纠纷|执行异议之诉|叛逃罪|签订、履行合同失职被骗罪|非法侵入计算机信息系统罪|伪造、盗窃、买卖、非法提供、非法使用武装部队专用标志罪|商检失职罪|盗窃、抢夺枪支、弹药、爆炸物、危险物质罪|招收公务员、学生徇私舞弊罪|别除权纠纷|进出口代理合同纠纷|组织残疾人、儿童乞讨罪|无因管理纠纷|用帐外客户资金非法拆借、发放贷款罪|出版合同纠纷|挪用公款罪|走私核材料罪|引诱幼女卖淫罪|传播性病罪|房屋拆迁安置补偿合同纠纷|广播电视|非法出售用于骗取出口退税、抵扣税款发票罪|股东名册记载纠纷|民用核设施损害责任纠纷|定期租船合同纠纷|网络域名注册合同纠纷|伪造货币罪|行纪合同纠纷|行政合同|武装叛乱、暴乱罪|船舶租用合同纠纷|质权纠纷|乡镇企业承包经营合同纠纷|信用证纠纷|劳务合同纠纷|公司合并纠纷|捕捞权纠纷|触电人身损害责任纠纷|海事海商纠纷|发明专利临时保护期使用费纠纷|妨害国境卫生检疫罪|发明创造发明人、设计人署名权纠纷|请求确认人民调解协议无效纠纷|破坏交通设施罪|拒不支付劳动报酬罪|船舶触碰损害责任纠纷|侵害作品广播权纠纷|非法制造、销售非法制造的注册商标标识罪|非法生产、买卖警用装备罪|资源|所有权纠纷|旅游合同纠纷|确认不侵害著作权纠纷|抵押合同纠纷|婚约财产纠纷|资助恐怖活动罪|抚养费纠纷|证券包销合同纠纷|侵犯商业秘密罪|煽动民族仇恨、民族歧视罪|悬赏广告纠纷|人事争议|加工合同纠纷|过失投放危险物质罪|请求撤销人民调解协议纠纷|刑讯逼供罪|行政管理范围|著作权权属纠纷|反革命集团罪|环境保护|煽动颠覆国家政权罪|因申请先予执行损害责任纠纷|违法保全赔偿|民用航空器损害责任纠纷|盗窃、抢夺武器装备、军用物资罪|其他劳动争议、人事争议|物权纠纷|农村房屋买卖合同纠纷|缔约过失责任纠纷|非法采矿罪|欺诈发行股票、债券罪|水路货物运输合同纠纷|高度危险作业损害赔偿纠纷|委托开立信用证纠纷|国有公司、企业、事业单位人员失职罪|融资租赁合同纠纷|知识产权质押合同纠纷|专利权权属纠纷|擅自发行股票、公司、企业债券罪|帮助犯罪分子逃避处罚罪|海上、通海水域财产损害责任纠纷|倒卖文物罪|放射性污染责任纠纷|证券投资基金回购合同纠纷|债权债务概括转移合同纠纷|商业贿赂不正当竞争纠纷|排除妨害纠纷|铁路电信服务合同纠纷|同业拆借纠纷|广播电视播放合同纠纷|经营秘密让与合同纠纷|故意杀人罪|相邻关系纠纷|妨害公务罪|国家机关及其工作人员职务侵权纠纷|邮政|遗失物返还纠纷|非法占有高度危险物损害责任纠纷|期货虚假信息责任纠纷|信用证开证纠纷|妨害清算罪|财产损害赔偿纠纷|非法拘禁罪|侵害作品发表权纠纷|姓名权纠纷|遗嘱继承纠纷|利用未公开信息交易罪|商标权转让合同纠纷|污染环境罪|行政救助|计算机软件著作权许可使用合同纠纷|欺诈客户责任纠纷|企业名称（商号）转让合同纠纷|税务|退伙纠纷|承揽合同纠纷|网络域名权属、侵权纠纷|车库纠纷|制作、复制、出版、贩卖、传播淫秽物品牟利罪|供用热力合同纠纷|刑事违法查封、扣押、冻结、追缴赔偿|暴力危及飞行安全罪|暴力干涉婚姻自由罪|乡政府|非法狩猎罪|投敌叛变罪|非法采伐、毁坏国家重点保护植物罪|计算机软件著作权权属纠纷|服务合同纠纷|申请中止支付保函项下款项|因申请诉前停止侵害专利权损害责任纠纷|对公司、企业人员行贿罪|容留他人吸毒罪|新增资本认购纠纷|企业借贷纠纷|因申请诉前证据保全损害责任纠纷|特殊的普通合伙纠纷|渎职罪|动产抵押权纠纷|旅店服务合同纠纷|股票交易纠纷|损害商业信誉、商品声誉罪|养老保险待遇纠纷|占有、使用高度危险物损害责任纠纷|组织播放淫秽音像制品罪|劳务派遣合同纠纷|辞退争议|申请撤销宣告公民死亡|非法占用农用地罪|战时拒绝、逃避服役罪|城乡建设|渔业|战时故意提供虚假敌情罪|船舶属具租赁合同纠纷|相邻土地、建筑物利用关系纠纷|金融诈骗罪|销售侵权复制品罪|技术出口合同纠纷|防卫过当损害赔偿纠纷|城市规划|义务帮工人受害责任纠纷|票据诈骗罪|诽谤罪|申请诉前停止侵害著作权|扶养纠纷|驻香港、澳门特别行政区军人执行职务侵权纠纷|违法运用资金罪|徇私舞弊减刑、假释、暂予监外执行罪|申请海事请求保全|申请承认和执行外国仲裁裁决|组织、领导、参加黑社会性质组织罪|私放在押人员罪|房屋拆迁|同居关系析产纠纷|铁路机车、车辆建造合同纠纷|船舶共有纠纷|引诱未成年人聚众淫乱罪|侵害患者知情同意权责任纠纷|广播组织权权属纠纷|海上、通海水域运输重大责任事故责任纠纷|集成电路布图设计许可使用合同纠纷|证券发行纠纷|强迫劳动罪|婚姻家庭纠纷|供用水合同纠纷|商品房预售合同纠纷|侵害作品放映权纠纷|商标权权属、侵权纠纷|铁路运输损害责任纠纷|阻碍执行军事职务罪|私分罚没财物罪|票据质权纠纷|普通破产债权确认纠纷|因申请海关知识产权保护措施损害责任纠纷|质押合同纠纷|走私武器、弹药罪|收买被拐卖的妇女、儿童罪|中介组织人员出具证明文件重大失实罪|执行|走私珍贵动物、珍贵动物制品罪|确认收养关系纠纷|管理人责任纠纷|海关|噪声污染侵权纠纷|集成电路布图设计专有权权属、侵权纠纷|船舶买卖合同纠纷|金融借款合同纠纷|居间合同纠纷|变更赡养关系纠纷|认定财产无主案件|不明抛掷物、坠落物损害责任纠纷|其他与公司、证券、保险、票据等有关的民事纠纷|违法发放贷款罪|提供虚假证明文件罪|船坞、码头建造合同纠纷|窝藏、转移、隐瞒毒品、毒赃罪|违法发放林木采伐许可证罪|其他婚姻家庭、继承纠纷|私放俘虏罪|侵害商业秘密纠纷|申请诉前停止侵害知识产权案件|违法刑事拘留赔偿|建筑物、构筑物倒塌损害责任纠纷|发明专利实施许可合同纠纷|非法生产、销售间谍专用器材罪|非法使用窃听、窃照专用器材罪|国债交易纠纷|其他知识产权与竞争纠纷|音像制品制作合同纠纷|非法收购、运输、出售珍贵、濒危野生动物、珍贵、濒危野生动物制品罪|买卖合同纠纷|运输合同纠纷|聘用合同争议|案外人执行异议之诉|人身自由权纠纷|船舶损坏空中设施、水下设施损害责任纠纷|申请海事证据保全|铁路委外劳务合同纠纷|战时残害居民、掠夺居民财物罪|网络服务合同纠纷|船舶营运借款合同纠纷|专利代理合同纠纷|虚假破产罪|商标使用许可合同纠纷|撤销婚姻纠纷|产品仓储者责任纠纷|股东知情权纠纷|非法留置船舶、船载货物、船用燃油、船用物料损害责任纠纷|占有排除妨害纠纷|垄断定价纠纷|生命权、健康权、身体权纠纷|股东出资纠纷|企业名称（商号）合同纠纷|证券承销合同纠纷|垄断协议纠纷|劫持航空器罪|走私贵重金属罪|侵犯财产罪|公司决议撤销纠纷|合伙企业财产份额转让纠纷|非法侵入住宅罪|包庇毒品犯罪分子罪|船舶碰撞损害责任纠纷|非法剥夺公民宗教信仰自由罪|紧急避险损害责任纠纷|期货实物交割纠纷|申请执行海事仲裁裁决|非法猎捕、杀害珍贵、濒危野生动物罪|装饰装修合同纠纷|拒绝提供间谍犯罪证据罪|非法捕捞水产品罪|与铁路运输有关的民事纠纷|技术委托开发合同纠纷|侮辱罪|建设用地使用权抵押权纠纷|银行卡纠纷|票据交付请求权纠纷|外观设计专利实施许可合同纠纷|拒绝交易纠纷|民政|赠与合同纠纷|非法出售发票罪|信用证议付纠纷|聚众扰乱公共场所秩序、交通秩序罪|探矿权转让合同纠纷|见义勇为人受害赔偿、补偿纠纷|提供侵入、非法控制计算机信息系统程序、工具罪|债权人代位权纠纷|公司盈余分配纠纷|违法使用武器、警械致人伤害、死亡赔偿|海域使用权纠纷|国际货物买卖合同纠纷|海上、通海水域运输船舶承包合同纠纷|侵害作品展览权纠纷|教育培训合同纠纷|船舶修理合同纠纷|非法批准征用、占用土地罪|地面、公共场所施工损害赔偿纠纷|重大责任事故罪|行政批准|被继承人债务清偿纠纷|企业出资人权益确认纠纷|提供劳务者受害责任纠纷|被撤销死亡宣告人请求返还财产纠纷|虚报注册资本罪|动植物检疫失职罪|诈骗罪|信用证诈骗罪|合同纠纷|侵害作品改编权纠纷|行政许可|车辆租赁合同纠纷|出售出入境证件罪|申请宣告公民恢复完全民事行为能力|战时违抗命令罪|非法持有、私藏枪支、弹药罪|录音录像制作者权权属纠纷|因申请诉前停止侵害植物新品种权损害责任纠纷|发现权纠纷|期货经纪合同纠纷|低价倾销不正当竞争纠纷|骗取贷款、票据承兑、金融票证罪|公司债券回购合同纠纷|信息电讯|赌博罪|海上、通海水域人身损害责任纠纷|帮助毁灭、伪造证据罪|借用合同纠纷|行政行为种类|放行偷越国（边）境人员罪|出售、非法提供公民个人信息罪|商标|船舶融资租赁合同纠纷|盗窃罪|选民资格案件|擅自设立金融机构罪|债务转移合同纠纷|放火罪|侵犯知识产权罪|合作创作合同纠纷|行政监察|侵害作品摄制权纠纷|生产、销售伪劣商品罪|恢复原状纠纷|抢夺、窃取国有档案罪|房屋买卖合同纠纷|军人违反职责罪|行政检查|技术合同纠纷|劳务派遣工作人员侵权责任纠纷|盗掘古人类化石、古脊椎动物化石罪|违反安全保障义务责任纠纷|高度危险责任纠纷|战时临阵脱逃罪|在建建筑物抵押权纠纷|海洋开发利用纠纷|逃避追缴欠税罪|煽动军人逃离部队罪|职工破产债权确认纠纷|确认票据无效纠纷|强迫他人吸毒罪|体育|强迫卖淫罪|知识产权权属、侵权纠纷|福利待遇纠纷|确认不侵害专利权纠纷|客户交易结算资金纠纷|技术秘密许可使用合同纠纷|传染病菌种、毒种扩散罪|生产、销售不符合安全标准的产品罪|噪声污染责任纠纷|侵害录音录像制作者权纠纷|建设工程价款优先受偿权纠纷|大气污染侵权纠纷|邻接权许可使用合同纠纷|贪污罪|背信运用受托财产罪|分期付款买卖合同纠纷|海上、通海水域保险合同纠纷|伪造、变造金融票证罪|商标合同纠纷|用人单位责任纠纷|航道、港口疏浚合同纠纷|挪用特定款物罪|动产质权纠纷|期货交易代理合同纠纷|存单质权纠纷|仲裁程序案件|适用特殊程序案件案由|在建船舶、航空器抵押权纠纷|掩饰、隐瞒犯罪所得、犯罪所得收益罪|国际铁路联运合同纠纷|牧业承包合同纠纷|伪造公司、企业、事业单位、人民团体印章罪|国家机关工作人员徇私舞弊罪|商品房销售合同纠纷|著作权转让合同纠纷|赡养费纠纷|非法吸收公众存款罪|非法向外国人出售、赠送珍贵文物罪|食品监管渎职罪|申请确认仲裁协议效力|虐待被监管人罪|铁路运输财产损害责任纠纷|附义务赠与合同纠纷|多式联运合同纠纷（由铁路法院受理的）|确认不侵害知识产权纠纷|决水罪|公益事业捐赠合同纠纷|猥亵儿童罪|船舶建造合同纠纷|非法收购、运输盗伐、滥伐的林木罪|阻碍军事行动罪|强迫卖血罪|相邻用水、排水纠纷|借记卡纠纷|过失爆炸罪|故意伤害罪|行政处罚|集成电路布图设计专有权权属纠纷|走私文物罪|申请拍卖扣押船用燃油及船用物料|战时拒绝、逃避征召、军事训练罪|聚众阻碍解救被收买的妇女、儿童罪|土地|行政征购|辞职争议|行政征用|定金合同纠纷|电力|发明权纠纷|工商|擅自出卖、转让军队房地产罪|失火罪|公司债券权利确认纠纷|非法获取公民个人信息罪|过失泄露国家秘密罪|逃离部队罪|组织卖淫罪|徇私舞弊不征、少征税款罪|相邻通风纠纷|上市公司收购纠纷|申请破产和解|执行判决、裁定滥用职权罪|公路交通|1979年前取消|农业承包合同纠纷|人民调解协议纠纷|共有人优先购买权纠纷|申请诉前财产保全|煽动暴力抗拒法律实施罪|诉讼、仲裁、人民调解代理合同纠纷|其他侵权责任纠纷|非法生产、买卖军用标志罪|保险诈骗罪|申请扣押船舶|公司章程或章程条款撤销纠纷|差别待遇纠纷|违规出具金融票证罪|其他|单独行政赔偿|审计|证券发行失败纠纷|经济补偿金纠纷|失业保险待遇纠纷|探望权纠纷|证券投资基金交易纠纷|基金份额质权纠纷|植物新品种合同纠纷|侵害其他著作财产权纠纷|收养关系纠纷|持有伪造的发票罪|非法收购、运输、加工、出售国家重点保护植物、国家重点保护植物制品罪|非法买卖制毒物品罪|商业秘密合同纠纷|营业信托纠纷|制作、贩卖、传播淫秽物品罪|占有消除危险纠纷|战时拒绝军事征用罪|暴力取证罪|植物新品种育种合同纠纷|非法采伐、毁坏珍贵树木罪|合同、无因管理、不当得利纠纷|逃避商检罪|财政|走私普通货物、物品罪|非法携带管制刀具、武器、爆炸物参加集会、游行、示威罪|仓单质权纠纷|发起人责任纠纷|农村土地承包合同纠纷|相邻通行纠纷|证券欺诈责任纠纷|公益信托纠纷|赡养纠纷|故意泄露军事秘密罪|期货交易纠纷|非法出卖、转让武器装备罪|建设用地使用权合同纠纷|组织越狱罪|侵害作品复制权纠纷|海上、通海水域保赔合同纠纷|破坏社会主义市场经济秩序罪|申请诉前停止侵害专利权|申请拍卖扣押船舶|非法种植毒品原植物罪|申请支付令|申请宣告公民恢复限制民事行为能力|专利权转让合同纠纷|隐藏物返还纠纷|操纵证券交易市场责任纠纷|申请设立海事赔偿责任限制基金|偷越国（边）境罪|土地承包经营权互换合同纠纷|特殊标志合同纠纷|吸收客户资金不入账罪|过失损坏武器装备、军事设施、军事通信罪|申请执行人执行异议之诉|一并行政赔偿|水利|商业受贿罪|有线电视服务合同纠纷|管道运输合同纠纷|技术转让合同纠纷|业主专有权纠纷|盗窃、侮辱尸体罪|破坏选举罪|过失投毒罪|拐骗儿童罪|侵害作品表演权纠纷|投放虚假危险物质罪|铁路运输人身损害责任纠纷|临时用地合同纠纷|私分国有资产罪|侵害作品署名权纠纷|技术咨询合同纠纷|殴打、虐待致伤、致死赔偿|非全日制用工纠纷|申请诉前证据保全|侵害作品出租权纠纷|危害公共安全罪|民事|内幕交易、泄露内幕信息罪|危害税收征管罪|航空运输人身损害责任纠纷|提供虚假财会报告罪|土地承包经营权入股合同纠纷|物业服务合同纠纷|网络域名转让合同纠纷|刑讯逼供致人伤害、死亡赔偿|妨害社会管理秩序罪|诱骗投资者买卖证券、期货合约罪|企业租赁经营合同纠纷|招摇撞骗罪|破坏环境资源保护罪|水上运输损害责任纠纷|非法制造、买卖、运输、储存危险物质罪|打击报复证人罪|非法提供麻醉药品、精神药品罪|与破产有关的纠纷|销售假冒注册商标的商品罪|走私、贩卖、运输、制造毒品罪|侵犯公民人身权利民主权利罪|战时窝藏逃离部队军人罪|申请变更监护人|海上、通海水域运输联营合同纠纷|强令违章冒险作业罪|消除危险纠纷|司法行政|质量监督|破坏生产经营罪|申请中止支付信用证项下款项|申请认可和执行台湾地区法院民事判决|借款合同纠纷|意外伤害保险合同纠纷|土地承包经营权出租合同纠纷|申请认可和执行香港特别行政区仲裁裁决|经营秘密许可使用合同纠纷|交通肇事罪|离婚后财产纠纷|公司设立纠纷|公证损害赔偿纠纷|申请海事强制令|医疗事故罪|海事债权确权纠纷|大气污染责任纠纷|生产、销售劣药罪|因申请诉中财产保全损害责任纠纷|有限合伙纠纷|建设工程合同纠纷|请求变更公司登记纠纷|非法携带枪支、弹药、管制刀具、危险物品危及公共安全罪|雇员受害赔偿纠纷|战时造谣扰乱军心罪|证券交易合同纠纷|强奸罪|行政赔偿|故意延误投递邮件罪|互易纠纷|组织、强迫、引诱、容留、介绍卖淫罪|光船租赁合同纠纷|信用证融资纠纷|侵害作品修改权纠纷|窝藏、转移、收购、销售赃物罪|保管合同纠纷|海上、通海水域拖航合同纠纷|破坏通讯设备罪|公司决议纠纷|企业兼并合同纠纷|埋藏物返还纠纷|建设工程施工合同纠纷|检疫|破坏计算机信息系统罪|用益物权确认纠纷|企业股份合作制改造合同纠纷|走私废物罪|侵犯著作权罪|劳动合同纠纷|绑架罪|农村建房施工合同纠纷|侵害广播组织权纠纷|偷税罪|伪造产地纠纷|雇用童工从事危重劳动罪|票据回购纠纷|侵害集成电路布图设计专有权纠纷|擅自使用知名商品特有名称、包装、装潢纠纷|行政征收|食品药品安全|重大飞行事故罪|民间委托理财合同纠纷|侵害企业出资人权益纠纷|巨额财产来源不明罪|伪造、变造、买卖国家机关公文、证件、印章罪|经济适用房转让合同纠纷|不报、谎报安全事故罪|不法地主罪|1979年至1997年间取消|防卫过当损害责任纠纷|限定交易纠纷|医疗产品责任纠纷|非法获取国家秘密罪|接送不合格兵员罪|建设用地使用权出让合同纠纷|邻接权转让合同纠纷|遗失武器装备罪|其他适用特殊程序案件案由|证券托管纠纷|堆放物倒塌致害责任纠纷|船舶代理合同纠纷|融资融券交易纠纷|宣告失踪、宣告死亡案件|投机倒把罪|清算责任纠纷|申请宣告公民限制民事行为能力|证券投资咨询纠纷|走私罪|船舶物料和备品供应合同纠纷|海难救助合同纠纷|过失损坏易燃易爆设备罪|非法占用耕地罪|职务技术成果完成人奖励、报酬纠纷|期货保证合约纠纷|铁路运营安全事故罪|能源|船舶拆解合同纠纷|物件脱落、坠落损害责任纠纷|公司、企业人员受贿罪|渔船承包合同纠纷|背叛国家罪|股权转让纠纷|公司分立纠纷|非法生产、买卖武装部队制式服装罪|申请认定财产无主|单位受贿罪|城市公交运输合同纠纷|委托代建合同纠纷|质押式证券回购纠纷|测试合同纠纷|侵占期货交易保证金纠纷|行政登记|离退休人员返聘合同纠纷|抢劫罪|信托纠纷|反革命宣传煽动罪|债权人撤销权纠纷|地质矿产|行贿罪|竞业限制纠纷|抢劫枪支、弹药、爆炸物、危险物质罪|公路货物运输合同纠纷|铁路货物、旅客、行李、包裹运输保险合同纠纷|返还原物纠纷|土地租赁合同纠纷|盗伐林木罪|生产、销售不符合卫生标准的食品罪|故意泄露国家秘密罪|申请确定选民资格|错误执行赔偿|证券登记、存管、结算纠纷|侵害出版者权纠纷|外资管理|非法采集、供应血液、制作、供应血液制品罪|相邻损害防免关系纠纷|公司减资纠纷|走私固体废物罪|隐私权纠纷|介绍贿赂罪|与公司、证券、保险、票据等有关的民事纠纷|虚假出资、抽逃出资罪|申请扣押船用燃油及船用物料|运送他人偷越国（边）境罪|建设用地使用权转让合同纠纷|妨害传染病防治罪|集成电路布图设计合同纠纷|驻香港、澳门特别行政区军人执行职务侵权责任纠纷|对非国家工作人员行贿罪|电信服务合同纠纷|破坏武器装备、军事设施、军事通信罪|逃汇罪|受贿罪|徇私舞弊发售发票、抵扣税款、出口退税罪|辩护人、诉讼代理人毁灭证据、伪造证据、妨害作证罪|专利权权属、侵权纠纷|故意毁坏财物罪|生产、销售有毒、有害食品罪|合资、合作开发房地产合同纠纷|出卖人取回权纠纷|遗弃罪|阻碍解救被拐卖、绑架妇女、儿童罪|知识产权合同纠纷|特殊类型的侵权纠纷|教育机构责任纠纷|申请诉前停止侵害注册商标专用权|航空|假冒注册商标罪|扶养费纠纷|劳动争议|徇私舞弊低价折股、出售国有资产罪|理货合同纠纷|土地承包经营权转包合同纠纷|过失损毁文物罪|变造货币罪|土壤污染责任纠纷|利用影响力受贿罪|妨害对公司、企业的管理秩序罪|公司证照返还纠纷|房屋登记|投降罪|非法制造、出售非法制造的发票罪|过失损坏电力设备罪|铁路运输人身、财产损害赔偿纠纷|洗钱罪|司法赔偿|损害债务人利益赔偿纠纷|骗购外汇罪|变更扶养关系纠纷|相邻采光、日照纠纷|信用卡诈骗罪|取回权纠纷|走私珍稀植物、珍稀植物制品罪|海运欺诈纠纷|技术服务合同纠纷|商标权权属纠纷|违法查封、扣押、冻结赔偿|金融凭证诈骗罪|铁路修建、管理和运输合同纠纷|证券返还纠纷|实用新型专利实施许可合同纠纷|技术成果完成人署名权、荣誉权、奖励权纠纷|走私假币罪|铁路行李运输合同纠纷|房地产价格评估合同纠纷|债券质权纠纷|其他海事海商纠纷|提单质权纠纷|劳动争议、人事争议|庆典服务合同纠纷|走私淫秽物品罪|出版歧视、侮辱少数民族作品罪|信用保险合同纠纷|继承纠纷|确认合同有效纠纷|专利权宣告无效后返还费用纠纷|故意损毁文物罪|票据保证纠纷|股票权利确认纠纷|航空旅客运输合同纠纷|海事请求担保纠纷|群众性活动组织者责任纠纷|传播淫秽物品罪|保险纠纷|变更抚养关系纠纷|重审无罪赔偿|危险物品肇事罪|单位行贿罪|垄断纠纷|违法司法拘传赔偿|电视购物合同纠纷|农业技术服务合同纠纷|证券认购纠纷|股权质权纠纷|其他物权纠纷|放射性污染侵权纠纷|追收抽逃出资纠纷|航次租船合同纠纷|因申请知识产权临时措施损害责任纠纷|船舶权属纠纷|行政补偿|非法持有毒品罪|过失泄露军事秘密罪|林业承包合同纠纷|行政撤销|滥用职权罪|医疗损害责任纠纷|票据损害责任纠纷|证券回购合同纠纷|申请承认和执行外国法院民事判决、裁定|外汇|国家机关工作人员签订、履行合同失职被骗罪|组织淫秽表演罪|资敌罪|虚假广告罪|中外合资经营企业承包经营合同纠纷|特许经营合同纠纷|土地承包经营权转让合同纠纷|铁路货物运输合同纠纷|报复陷害罪|聚众冲击军事禁区罪|网络域名合同纠纷|编造并传播证券、期货交易虚假信息罪|战时自伤罪|联合运输合同纠纷|房屋租赁合同纠纷|其他行政管理|其他人格权纠纷|海事担保合同纠纷|违规披露、不披露重要信息罪|植物新品种申请权转让合同纠纷|海运集装箱保管合同纠纷|担保物权确认纠纷|申请诉中财产保全|危害国家安全罪|见义勇为人受害责任纠纷|妨害司法罪|破产撤销权纠纷|治安|婚姻家庭、继承纠纷|拐卖妇女、儿童罪|保险经纪合同纠纷|烟草专卖|网络侵权责任纠纷|侵害保护作品完整权纠纷|地役权纠纷|消防责任事故罪|请求撤销个别清偿行为纠纷|伪造、出售伪造的增值税专用发票罪|包庇、纵容黑社会性质组织罪|非法经营罪|提供伪造、变造的出入境证件罪|徇私枉法罪|林木折断损害责任纠纷|行政不作为|为境外窃取、剌探、收买、非法提供军事秘密罪|侵害特殊标志专有权纠纷|投放危险物质罪|铁路旅客、行李、包裹运输合同纠纷|证券上市保荐合同纠纷|因恶意提起知识产权诉讼损害责任纠纷|打击报复会计、统计人员罪|房地产咨询合同纠纷|破坏易燃易爆设备罪|动产浮动抵押权纠纷|投毒罪|擅自出卖、转让国有档案罪|船员劳务合同纠纷|企业名称（商号）使用合同纠纷|公司解散纠纷|滥伐林木罪|信用证转让纠纷|船舶污染损害责任纠纷|建设用地使用权纠纷|过失以危险方法危害公共安全罪|委托理财合同纠纷|遗弃武器装备罪|行政|文化|技术合作开发合同纠纷|彩票、奖券纠纷|串通投标不正当竞争纠纷|物件损害责任纠纷|委托创作合同纠纷|窝藏、包庇罪|分裂国家罪|故意提供不合格武器装备、军事设施罪|民事、行政枉法裁判罪|证券上市合同纠纷|采矿权转让合同纠纷|抗税罪|保险代理合同纠纷|饲养动物损害责任纠纷|婚姻无效纠纷|教育|重大劳动安全事故罪|海上、通海水域旅客运输合同纠纷|虚假宣传纠纷|产品生产者责任纠纷|集资诈骗罪|遗赠纠纷|票据代理纠纷|专利|生产、销售假药罪|修理合同纠纷|汇票回单签发请求权纠纷|伪造、变造居民身份证罪|非法获取军事秘密罪|再保险合同纠纷|为亲友非法牟利罪|电子废物污染责任纠纷|保险费纠纷|行政复议|走私国家禁止进出口的货物、物品罪|挂靠经营合同纠纷|期货透支交易纠纷|侵害表演者权纠纷|健康保险合同纠纷|刑事|确认合同无效纠纷|拒不执行判决、裁定罪|铁路修建合同纠纷|隐瞒境外存款罪|取水权纠纷|非法处置查封、扣押、冻结的财产罪|股东损害公司债权人利益责任纠纷|申请确定监护人|非法出售增值税专用发票罪|以危险方法危害公共安全罪|储蓄存款合同纠纷|申请海事债权登记与受偿|证券交易代理合同纠纷|强迫职工劳动罪|生产、销售伪劣产品罪|商业诋毁纠纷|生产、销售不符合安全标准的食品罪|金融衍生品种交易纠纷|对外追收债权纠纷|建设工程勘察合同纠纷|植物新品种实施许可合同纠纷|破坏金融管理秩序罪|财产保险合同纠纷|租赁合同纠纷|土地承包经营权抵押权纠纷|行政奖励|招标投标买卖合同纠纷|期货欺诈责任纠纷|种植、养殖回收合同纠纷|请求确认人民调解协议效力|擅自进口固体废物罪|一般取回权纠纷|质量监督检验检疫|指使部属违反职责罪|损害公司利益责任纠纷|生育保险待遇纠纷|海上、通海水域打捞合同纠纷|侵害外观设计专利权纠纷|出具证明文件重大失实罪|凭样品买卖合同纠纷|中外合作经营企业承包经营合同纠纷|技术转化合同纠纷|生产、销售伪劣农药、兽药、化肥、种子罪|申请宣告公民无民事行为能力|申请公司清算|侵害作品汇编权纠纷|骗取出口退税罪|计算机软件开发合同纠纷|丢失枪支不报罪|渔业承包合同纠纷|聚众持械劫狱罪|承包地征收补偿费用分配纠纷|申请保全案件|冒充军人招摇撞骗罪|请求确认债务人行为无效纠纷|非法制造、买卖、运输、邮寄、储存枪支、弹药、爆炸物罪|敲诈勒索罪|申请撤销认定财产无主|公共场所管理人责任纠纷|海上、通海水域污染损害责任纠纷|非法买卖、运输核材料罪|枉法裁判罪|业主撤销权纠纷|申请破产重整|妨害文物管理罪|采矿权纠纷|票据追索权纠纷|侵害作品信息网络传播权纠纷|证券资信评级服务合同纠纷|仲裁程序中的证据保全|植物新品种申请权权属纠纷|请求公司收购股份纠纷|非法买卖、运输、携带、持有毒品原植物种子、幼苗罪|遗弃伤病军人罪|房地产开发经营合同纠纷|抢夺罪|非法集会、游行、示威罪|再审无罪赔偿|枉法追诉、裁判罪|外商独资企业承包经营合同纠纷|高度危险活动损害责任纠纷|枉法仲裁罪|铁路运输延伸服务合同纠纷|扰乱无线电通讯管理秩序罪|环境污染侵权纠纷|行政执行|操纵证券、期货市场罪|监护权纠纷|水污染侵权纠纷|仲裁程序中的财产保全|表演者权权属纠纷|军人叛逃罪|转质权纠纷|海上、通海水域行李运输合同纠纷|航空运输财产损害责任纠纷|纵向垄断协议纠纷|占有物损害赔偿纠纷|因申请诉前财产保全损害责任纠纷|协助组织卖淫罪|不动产登记纠纷|行政监督|债权转让合同纠纷|公司决议效力确认纠纷|检验合同纠纷|共同海损纠纷|卫生|流氓罪|无罪逮捕赔偿|财产损失保险合同纠纷|监护人责任纠纷|已删案由|集成电路布图设计创作合同纠纷|物权确认纠纷|植物新品种权转让合同纠纷|申请公示催告|操纵证券、期货交易价格罪|破坏集会、游行、示威罪|提供劳务者致害责任纠纷|过失提供不合格武器装备、军事设施罪|公路旅客运输合同纠纷|证券虚假陈述责任纠纷|土地承包经营权纠纷|编造、故意传播虚假恐怖信息罪|国家赔偿|遗赠扶养协议纠纷|挪用资金罪|环境污染责任纠纷|股东资格确认纠纷|伪造、倒卖伪造的有价票证罪|非法处置进口的固体废物罪|对单位行贿罪|著作权许可使用合同纠纷|公示催告程序案件|技术秘密让与合同纠纷|强迫交易罪|私自开拆、隐匿、毁弃邮件、电报罪|申请船舶优先权催告|水上运输人身损害责任纠纷|侵害发明专利权纠纷|横向垄断协议纠纷|破坏电力设备罪|国有公司、企业、事业单位人员滥用职权罪|操纵期货交易市场责任纠纷|侵权责任纠纷|过失损坏广播电视设施、公用电信设施罪|侵占罪|非法低价出让国有土地使用权罪|失职致使在押人员脱逃罪|大型群众性活动重大安全事故罪|肖像权纠纷|玩忽职守罪|担保物权纠纷|行政给付|拍卖合同纠纷|入伙纠纷|失职造成珍贵文物损毁、流失罪|错判罚金、没收财产赔偿|为境外窃取、剌探、收买、非法提供国家秘密、情报罪|共有纠纷|非法组织卖血罪|违法司法罚款赔偿|海事诉讼特别程序案件|追索劳动报酬纠纷|异议登记不当损害责任纠纷|专利申请权转让合同纠纷|放纵制售伪劣商品犯罪行为罪|破坏性采矿罪|滥用管理公司、证券职权罪|项目转让合同纠纷|与公司有关的纠纷|补偿贸易纠纷|侵害实用新型专利权纠纷|行政作为|组织、领导传销活动罪|伪造车票、船票、邮票、税票、货票罪|土地承包经营权继承纠纷|妨害作证罪|证券代销合同纠纷|新闻出版|执行判决、裁定失职罪|动植物检疫徇私舞弊罪|商标代理合同纠纷|委托合同纠纷|非法出具金融票证罪|聚众扰乱社会秩序罪|邮寄服务合同纠纷|保险人代位求偿权纠纷|同居关系纠纷|计算机软件著作权转让合同纠纷|爆炸罪|生产、销售不符合标准的医用器材罪|定作合同纠纷|拒传、假传军令罪|工程重大安全事故罪|公司增资纠纷|铁路包裹运输合同纠纷|其他科技成果权纠纷|国有资产|侵害商标权纠纷|申请诉中证据保全|公安|持有、使用假币罪|植物新品种权权属、侵权纠纷|国债权利确认纠纷|损害铁路赔偿纠纷|徇私舞弊不移交刑事案件罪|过失损坏交通工具罪|非法获取计算机信息系统数据、非法控制计算机信息系统罪|申请扣押船载货物|违法提供出口退税凭证罪|法律服务合同纠纷|申请撤销仲裁裁决|船舶抵押合同纠纷|奸淫幼女罪|社会保险纠纷|非法购买增值税专用发票、购买伪造的增值税专用发票罪|请求履行人民调解协议纠纷|间谍罪|隐匿、故意销毁会计凭证、会计帐簿、财务会计报告罪|申请破产清算|非国家工作人员受贿罪|确认劳动关系纠纷|侵害企业名称（商号）权纠纷|倒卖车票、船票罪|解除收养关系纠纷|出租汽车运输合同纠纷|相邻污染侵害纠纷|行政受理|组织出卖人体器官罪|转继承纠纷|职工权益清单更正纠纷|因申请诉中证据保全损害责任纠纷|有奖销售纠纷|中外合资经营企业合同纠纷|知识产权质权纠纷|已删去案由|金融|行政划拔|广告合同纠纷|申请为失踪人财产指定、变更代管人|海上、通海水域货运代理合同纠纷|企业债权转股权合同纠纷|离婚后损害责任纠纷|交通运输|企业公司制改造合同纠纷|督促程序案件|侵害作品发行权纠纷|对外国公职人员、国际公共组织官员行贿罪|申请承认与执行法院判决、仲裁裁决案件|逃避动植物检疫罪|货运代理合同纠纷|公司关联交易损害责任纠纷|破坏社会主义经济秩序罪|港口作业重大责任事故责任纠纷|质量检验|期货内幕交易责任纠纷|申请执行知识产权仲裁裁决|地面施工、地下设施损害责任纠纷|贷款诈骗罪|抚养纠纷|土地承包经营权抵押合同纠纷|骗取出境证件罪|道路|破坏监管秩序罪|航空货物运输合同纠纷|公证损害责任纠纷|教育设施重大安全事故罪|集成电路布图设计专有权转让合同纠纷|侵害计算机软件著作权纠纷|仓储合同纠纷|农业|行政允诺|抵押权纠纷|非法持有国家绝密、机密文件、资料、物品罪|虚假登记损害责任纠纷|同居关系子女抚养纠纷|证券投资基金权利确认纠纷|过失决水罪|用益物权纠纷)$");
        Pattern amountPattern = Pattern.compile(".*?(扶养费纠纷|劳动争议|出售国有资产罪|理货合同纠纷|土地承包经营权转包合同纠纷)$");
        Matcher amountMatcher = amountPattern.matcher("原告何建与被告易思博南京分公司劳动争议纠纷");
        if (amountMatcher.find()) {
            System.out.println(amountMatcher.group(1));
            //System.out.println(amountMatcher.group(2));
        }


    }

    public static void parseFilesSync(RuleJson rule, String folder) throws Exception {

        File dir = new File(folder);

        List<Future> futures = new ArrayList<>();
        for (File file : dir.listFiles()) {
            ParseContext context = new ParseContext();
            try {
                Document doc = Jsoup.parse(file, "GBK");
                System.out.println(file.getName());
                Element element = doc.getElementById("DivContent");
                Elements elements = element.children();
                List<String> statements = new ArrayList<>();

                context.addResult("filename", file.getName());
                for (Element oneElement : elements) {
                    statements.add(oneElement.ownText());
                    context.addResult("rawdata", oneElement.ownText());
                }

                context.setCurrentState("start");
                ParseStateMachine stateMachine = new ParseStateMachine(rule);
                stateMachine.run(context, statements);
                //file.exists();

            } catch (Exception e) {
                System.out.println(file.getName());
                e.printStackTrace();
            }

        }
    }
}
