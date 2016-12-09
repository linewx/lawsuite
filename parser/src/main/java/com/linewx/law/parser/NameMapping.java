package com.linewx.law.parser;

import org.apache.commons.lang3.ObjectUtils;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by luganlin on 11/19/16.
 */
public class NameMapping {
    public static void main(String argv[]) {

        String.join("\n", "");
        /*String a = "test-";
        String[] te = a.split("-");
        System.out.println(a.contains(""));*/
    }

    public static Map<String, String> names = new LinkedHashMap<>();

    public static String get(String field) {
        return names.getOrDefault(field, field);
    }


    static {
        /********************* 一审规则 *************************/

        /**
         * 1、标题“AAAA与BBBB”
         * 2、原告栏中，“原告:AAAA，住所地。。。。。。”
         * 3、原告有可能会有多个，但标题中可能不会全部出现，原告栏中会逐一出现，分行显示
         */
        names.put("accuser", "原告");


        names.put("accuserLegalEntity", "原告法人代表");
        /**
         * 原告栏中，“委托代理人”后面，到“，”且“，”后面包含“律师两个字”；
         * 原告律师可能出现多个，分行列明，规律同上；
         */
        names.put("accuserLawyer", "原告律师");

        /**
         * 原告栏中，原告律师后面，紧跟逗号以后，“***律师事务所”，在“律师”之前，
         * 原告律师的律师事务所有可能出现不同的两家，不同的律师事务所分行显示；
         */
        names.put("accuserLawyerOffice", "原告律师的律师事务所");

        /**
         *  1、标题“AAAA与BBBB”
         *  2、被告栏中，“被告BBBB，住所地。。。。”
         *  3、被告会有多个，多到7、8 个也可能，但标题栏中不会全部显示；
         *  4、不同被告分行显示；
         */
        names.put("defendant", "被告");

        names.put("defendantLegalEntity", "被告法人代表");


        /**
         *  被告栏中，“委托代理人”后面，到“，”且“，”后面包含“律师两个字”；被告律师也可能出现两个，分行列明，规律相同；
         */
        names.put("defendantLawyer", "被告律师");


        /**
         *  被告栏中，被告律师后面，紧跟逗号以后，“***律师事务所”，在律师以前；有可能出现不同的两家，不同的律师事务所分行显示；
         */
        names.put("defendantLawyerOffice", "被告律师的律师事务所");


        /**
         * 根据原告名称识别，具体规则另附，要求企业属性和行业表述可以组合复选。
         */
        names.put("accuserProperty", "原告企业类别和行业属性");

        /**
         * 根据被告名称识别，具体规则另附，组合复选同上。
         */
        names.put("defendantProperty", "被告企业类别和行业属性");

        names.put("judge", "法官");

        /**
         *  审判人员栏中：在第一行中，

         “审判长”，“审判员”或者“代理审判员”后面的名字。有＂审判长＂时，后面的姓名(2～4汉字)，无＂审判长＂时，＂审判员＂或＂代理审判员＂后面的姓名。主审法官具备唯一性。
         */
        names.put("mainJudge", "主审法官");

        names.put("secondaryJudge", "非主审法官");

        /**
         * 审判人员栏中，第二行，“审判员”或“代理审判员”后面的名字。＂书记员＂往上，审判员XXX或代理审判员XXX，先出现的。
         */
        names.put("secondaryJudge1", "非主审法官1");


        /**
         * 非主审法官审判员2
         审判人员栏中，第三行，“审判员”或“代理审判员”后面的名字。＂书记员＂往上，审判员XXX或代理审判员XXX，后出现的。
         */
        names.put("secondaryJudge2", "非主审法官2");


        /**
         *
         审判人员栏中的日期，有汉字的“****年 * 月 **日”的格式，也可能为XXXX年XX月XX日格式。改成日期格式便于统计检索，可能在书记员前或后，距离不超过3行。
         */
        names.put("date", "判决日期");


        /**
         * 审判人员栏中：“书记员三字”以后。有可能书记员和判决日期前后顺序不同
         */
        names.put("clerk", "书记员");

        /**
         * 正文首段，第一次出现。“原告 **诉被告 ***”后，XXXX“纠纷一案”，XXXX即为案由。被告可能有多个，在最后一个被告名字以后，“纠纷一案”以前就是案由。如果有标题，标题＂AAAA与BBBBXXXX纠纷YY判决书＂，
         XXXX纠纷即为案由，具体案由名称分类依据OPENLAW，案由位于首页。
         */

        names.put("reason", "案由");


        /**
         *  案号／案件类型／文书类型
         *   1、标题后，正文“原告”前，“（XXXX）XXXXXX字第XXXX号”这一行整体就是案号。2、＂民＂或＂商＂代表民商事案件，＂刑＂代表刑事案件，＂行＂代表行政案件，＂赔＂代表赔偿案件，＂执＂代表执行案件。3、根据前述案件类型，
         分民（商)～民事判决书丶民事裁定书丶民事调解书丶民事决定书丶执行裁定书丶刑事判决书丶行政裁定书丶行政判决书丶刑事附带民事判决书；刑～
         刑事判决书丶刑事裁定书丶刑事附带民事判决书丶刑事附带民事裁定书丶民事裁定书丶刑事决定书丶执行裁定书丶民事判决书丶刑事附带民事决定书；行～
         行政裁定书丶行政判决书丶执行裁定书丶国家赔偿裁定书丶国家赔偿决定书丶行政决定书丶民事判决书丶民事裁定书丶刑事裁定书丶国家赔偿决定书丶刑事判决书；赔～国家赔偿决定书；执～
         执行裁定书丶执行决定书丶民事裁定书丶民事判决书丶刑事裁定书丶执行判决书丶行政裁定书丶刑事决定书丶民事决定书丶刑事判决书；分别列举

         */
        names.put("number", "案号");
        names.put("caseType", "案件类型");
        names.put("instrumentType", "文书类型");

        /**
         * 立案年份
         * 案号中，“（）”里面，表示立案年份
         */

        names.put("suiteDate", "立案年份");

        /**
         * 案号中的“初”或“终”，后面是“字”，“初”表示一审，“终”表示“二审”；
         */
        names.put("level", "审级");

        /**
         *
         标题中第二句话，“**判决书”中 ****以“法院”结束的内容；＂法院:
         XXXXXX法院＂
         */

        names.put("court", "法院");

        /**
         * 仅限一审案件中，正文里面，第一个出现的“案件受理费XXXX元”，在元以前的金额为案件受理费；
         */
        names.put("cost", "案件受理费");

        /**
         * 诉请金额

         按照以下方式计算：

         通过案件受理费计算；在不需计算时，写入诉请金额为0，并写入诉请金额忽略标准：


         规则：针对一审民事案件：标题中带“一审民事判决书”，案号中有“民初字”


         一、忽略部分案由：

         第一类：婚姻家庭、继承纠纷——
         v: 0020201
         案由包括：婚约财产纠纷 离婚纠纷 离婚后财产纠纷 离婚后损害责任纠纷 婚姻无效纠纷 撤销婚姻纠纷 夫妻财产约定纠纷 同居关系纠纷 抚养纠纷 扶养纠纷 赡养纠纷 收养关系纠纷 监护权纠纷 探望权纠纷 分家析产纠纷，
         案件受理费大于等于人民币2000元的除外；

         00202,00201,00206,00302
         第二类：人格权纠纷——
         v: 0020101
         案由包括：生命权、健康权、身体权纠纷 姓名权纠纷 肖像权纠纷 名誉权纠纷 荣誉权纠纷 隐私权纠纷 婚姻自主权纠纷 人身自由权纠纷 一般人格权纠纷  ，案件受理费大于等于人民币1000元以上的除外；

         3~4 00206

         第三类：劳动争议纠纷——
         v: 0020601
         案由包括：劳动合同纠纷 社会保险纠纷 福利待遇纠纷

         第四类：人事争议案件—
         v: 0020602
         案由包括：人事争议


         第五类：著作权合同纠纷

         v: 00301,00302
         案由包括：商标合同纠纷 专利合同纠纷 植物新品种合同纠纷 集成电路布图设计合同纠纷 商业秘密合同纠纷 技术合同纠纷 特许经营合同纠纷 企业名称（商号）合同纠纷 特殊标志合同纠纷 网络域名合同纠纷 知识产权质押合同纠纷
         著作权权属、侵权纠纷 商标权权属、侵权纠纷 专利权权属、侵权纠纷 植物新品种权权属、侵权纠纷 集成电路布图设计专有权权属、侵权纠纷 侵害企业名称（商号）权纠纷 侵害特殊标志专有权纠纷 网络域名权属、侵权纠纷 发现权纠纷
         发明权纠纷 其他科技成果权纠纷 确认不侵害知识产权纠纷 因申请知识产权临时措施损害责任纠纷 因恶意提起知识产权诉讼损害责任纠纷 专利权宣告无效后返还费用纠纷  ，案件受理费人民币大于等于2500元以上的除外；


         二、倒算公式

         按照下述计算规则倒测——

         超过1万元至10万元的部分，按照2 .5 % 交纳；

         超过10万元至20万元的部分，按照2 % 交纳；

         超过20万元至50万元的部分，按照1 .5 % 交纳；

         超过50万元至100万元的部分，按照1 % 交纳；

         超过100万元至200万元的部分，按照0 .9 % 交纳；

         超过200万元至500万元的部分，按照0 .8 % 交纳；

         超过500万元至1000万元的部分，按照0 .7 % 交纳；

         超过1000万元至2000万元的部分，按照0 .6 % 交纳；

         超过2000万元的部分，按照0 .5 % 交纳。
         */

        names.put("amount", "诉请金额");

        /**
         * 诉请金额忽略标志
         */
        names.put("ignoreAmount", "诉请金额忽略标志");

        /**
         * 受理费是否减半
         *
         * 如果是，做个标记，
         出现诉请金额以后，出现“减半收取”四个字，就是减半
         */
        names.put("discountHalf", "受理费减半");


        /**
         *  1、“由被告某某某（和某某某共同）承 / 负担”，没有金额的话，被告受理费为全部受理费，下面原告胜率为100％

         2、如果有“由被告（和某某某共同）****负担 ****元”，则金额为被告负担受理费
         */
        names.put("costOnDefendant", "被告负担受理费");

        /**
         * 1、“由原告某某某（和某某某共同）承 / 负担”，没有金额的话，原告受理费为全部受理费，下面原告胜率为0％

         2、如果有“由原告（和某某某共同）****负担 ****元”，则金额为原告负担受理费
         */
        names.put("costOnAccuser", "原告负担受理费");


        /**
         *  1. 如果原告和被告胜率不为100％或0％时
         2. 被告负担受理费 /（原告负担受理费 + 被告负担受理费）
         3. 原告负担受理费＋被告负担受理费＝案件受理费（当案件受理费减半收取为＂是＂，该项应为减半后的案件受理费)，该项一定成立，否则前述原丶被告负担受理费的识别有误，应显示原丶被告负担案件受理费和原告丶被告胜率显示＂出错＂标记。
         */

        names.put("accuserWinPer", "本案原告胜率");

        /**
         *  1 - 本案原告胜率
         */
        names.put("defendantWinPer", "本案被告胜率");

        /**
         * 在正文中，倒数三页内，＂书记员＂往上1～2 页内，“判决如下”与“未按本判决指定的期间履行给付金钱义务”之间
         XXXX元、YYYY元、ZZZZ元三个最大金额之和，这三个数字不能有重复，重复取其中一个。必须是＂元＂之前的数字。如果不足前述三个金额(比如只有一个金额时，只取该金额，只有两个金额时，取两金额之和)
         */

        names.put("accuserAmount", "原告主要诉请获支持金额");

        /**
         * 原告主要诉请获支持金额 / 诉请金额x100％
         */
        names.put("accuserAmountPer", "原告主要诉请获支持率");


        /**
         *  1 - 原告主要诉请获支持率
         */
        names.put("defendantAmountPer", "被告抗辩获支持率");


        /**
         * 1、标题中含“调解书”，正文中含“自愿达成如下调解协议”；
         2、一审调解结案不统计原告主要诉请获支持金额；
         3、一审调解书原、被告胜率、原告主要诉请获支持率、被告抗辩获支持率统一为50 %；
         */
        names.put("firstConciliation", "一审调解结案");


        /********************* 二审规则 *************************/
        /**
         *
         *
         *二审文件解读：
         *二审文件审级为‘终’
         *从文件结构来看，和一审文件相比：“原告”变成“上诉人”，“被告”换成“被上诉人”，与一审文件关联，使用“不服”+一审法院 + 一审案号；一审的原告和被告都可以做二审的上诉人。
         */

        /**
         * 0. 关联案件组
         *“不服”+一审法院 + 一审案号
         */
        names.put("relatedNumber", "关联案件组");
        /**
         * 上诉人是否原审原告
         *上诉人在关联一审案件中，如果是原告，上诉人就是原审原告，如果不是原告，则不是原审原告
         *
         */
       names.put("appellantIsAccuser", "上诉人是否原审原告");

        /**
         * 1
         *案号
         *标题后，“上诉人”前，“（XXXX）XXXXXX字第XXXX号”这一行整体就是案号
         */

        /**2
         *立案年份
         *案号中，“（）”里面，表示立案年份
         */
        /**
         3
         审级

         案号中的“初”或“终”，后面是“字”，“初”表示一审，“终”表示“二审”
         */

        /**
         4

         法院名称

         标题中第二句话，“**书（或令）”中 ****以“法院”结束的内容，
         */
        /**
         5

         上诉人

         上诉人为：“AAAA”

         4、标题“AAAA与BBBB”

         5、上诉人栏中，“上诉人:
         AAAA，住所地。。。。。。”

         6、上诉人会有多个

         */

        /**
         6

         上诉人律师

         上诉人栏中，“委托代理人”后面，到“，”且“，”后面包含“律师两个字”

         */
        /**
         7

         上诉人律师的律师事务所

         上诉人栏中，上诉人律师后面，紧跟逗号以后，“***律师事务所”，在“律师”两字以前
         */
        /**
         8

         被上诉人

         被上诉人（BBBB）

         5、标题“AAAA与BBBB”

         6、被上诉人栏中，“被上诉人BBBB，住所地。。。。”

         7、被上诉人会有多个，多到7、8 个也可能
         */
        /**
         9

         被上诉人律师

         被上诉人栏中，“委托代理人”后面，到“，”且“，”后面包含“律师两个字”
         */
        /**
         10

         被上诉人律师的律师事务所

         被上诉人栏中，被上诉人律师后面，紧跟逗号以后，“***律师事务所”，在律师以前
         */
        /**
         11

         上诉人委托代理人

         上诉人信息以下，“委托代理人AAAA”
         */
        /**
         12

         被上诉人委托代理人

         被上诉人信息以下，“委托代理人AAAA”
         */
        /**
         13

         主审法官

         审判人员栏中：在第一行中，

         “审判长”，“审判员”或者“代理审判员”后面的名字

         */
        /**
         14

         审判员1

         审判人员栏中，第二行，“审判员”或“代理审判员”后面的名字
         */
        /**
         15

         审判员2

         审判人员栏中，第三行，“审判员”或“代理审判员”后面的名字
         */
        /**
         16

         判决日期：

         审判人员栏中的日期，有汉字的“****年 * 月 **日”的格式，改成日期格式便于统计检索
         */
        /**
         17

         书记员

         审判人员栏中：“书记员三字”以后。有可能书记员和判决日期前后顺序不同
         */
        /**
         18

         审判类型

         二审维持、二审驳回、二审改判、

         二审调解三种类型

         正文里面；“驳回上诉，维持原判”为识别项，则是二审维持


         正文里面：“发回××××人民法院重审”为二审驳回


         正文里面：“判决如下”和“终审判决”之间，有“撤销（案号）***判决”这样句子，就是二审改判


         正文里面：标题中含“调解书”，正文中含“自愿达成如下调解协议”，就是二审调解
         */

        names.put("judgeType", "审判类型");

        /**
         19

         二审律师缺勤标记

         在可追踪到二审判决的情况下，如在一审中出现过的律师(不分原被告），在二审中不再出现的情况，需在一审案件信息统计中对该律师打＂二审缺勤＂标记
         */
        /**
         20

         二审缺勤率


         某律师二审缺勤次数／可统计的有该律师参与的＂成套上诉案件＂总数
         */
        /**
         21


         关联案件组

         具备＂关联一审案号＂，且可统计到一审案件的二审案件
         */
        /**
         22

         二审调解结案

         1、标题中含“调解书”，正文中含“自愿。。。。达成如下。。。。协议”（上述关键词出现在同一句话中，中间有其他词或无其他词均有可能）；

         2、二审调解书不统计原告主要诉请获支持金额；

         3、二审调解书原、被告胜率、原告主要诉请获支持率、被告抗辩获支持率统一为50 %；

         */
        names.put("finalConciliation", "二审调解结案");

        /************** 再审 *****************/
        /**

         再审和审判监督案件文件解读：

         1、再审和审判监督案件的案号中包括以下文字之一：“监”、“申”、“抗”、“再”、“提”，如“民申”、“民监”、“民抗”、“民再”、“民提”；

         2、再审和审判监督案件的文本类型同样包括判决书、裁定书、决定书、调解书、通知书、令；
         */


/**
 1

 关联案件组

 1、先与二审关联——二审法院 + 二审案号；

 2、再与一审关联——通过前一步关联到的二审文书中提及的：“不服” + 一审法院 + 一审案号

 3、每组关联案件组的文书数量多数有可能超过3份；
 */
        /**

         2

         案号

         紧接标题后，“（XXXX）XXXXXX字第XXXX号”这一行整体就是案号
         */
        /**

         3

         立案年份

         案号中第一个括号内的四位数年份
         */
        /**
         4

         受理法院

         标题中第二句话，“**书（或令）”中 ****以“法院”结束的内容，
         */
        /**
         5

         再审申请人

         “再审申请人“栏中，“再审申请人:
         AAAA，住所地。。。。。。”

         再审申请人可能会有多个
         */
        /**
         6

         再审申请人律师 / 再审申请人律师事务所

         再审申请人栏中，“委托代理人”后面，到“，”且“，”后面包含“律师两个字”；再审申请人律师后面，紧跟逗号以后，“***律师事务所”，在“律师”两字以前；每个再审申请人对应的律师和律师事务所均可能有1 - 2 个；
         */
        /**
         7

         主审法官


         审判人员栏中：在第一行中，

         “审判长”，“审判员”或者“代理审判员”后面的名字
         */
        /**
         8

         再审金额


         同关联案件组中一审的“诉请金额”，具体识读规则同一审
         */
        /**
         9

         “再审申请人律师后加入”标记


         以关联案件组为单位，如果再审申请人律师在一审、二审中均未出现过，则显示“再审申请人律师后加入”
         */
        /**
         10

         “再审申请人律师后加入率”


         就某一律师而言，显示“再审申请人律师后加入”标记的次数 / 作为“再审申请人律师”出现的全部次数 * 100 %
         }
         */

    }
}
