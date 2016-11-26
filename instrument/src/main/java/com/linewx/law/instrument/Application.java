package com.linewx.law.instrument;

import com.google.gson.Gson;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.parser.InstrumentParser;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.ParseStateMachine;
import com.linewx.law.parser.json.RuleJson;
import org.apache.commons.cli.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
@SpringBootApplication
public class Application implements CommandLineRunner{
    private static Logger logger = LoggerFactory.getLogger(Application.class);
    private static AtomicLong total = new AtomicLong(0L);
    private static AtomicLong error = new AtomicLong(0L);
    private static AtomicLong unsupported = new AtomicLong(0L);

    public static void main(String args[]) throws Exception{
        SpringApplication.run(Application.class, args);
    }

    public void run(String ...argv) throws Exception {
        Options options = new Options();

        Option fileOption = new Option("f" , true, "single file to parse");
        fileOption.setRequired(false);
        options.addOption(fileOption);

        Option folderOption = new Option("d", true, "folder to parse");
        folderOption.setRequired(false);
        options.addOption(folderOption);

        Option ruleOption = new Option("r",  true, "parser rule location");
        ruleOption.setRequired(true);
        options.addOption(ruleOption);

        CommandLineParser commandLineParser = new BasicParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine commandLine;

        try {
            commandLine = commandLineParser.parse(options, argv);
        }catch(ParseException e) {
            System.out.println(e.getMessage());
            helpFormatter.printHelp("MainApp", options);

            System.exit(1);
            return;
        }

        String ruleLocation = commandLine.getOptionValue("r");
        RuleJson rule = loadRule(ruleLocation);

        String fileName = commandLine.getOptionValue("f");
        if (fileName != null) {
            parseFile(rule, fileName);
            return;
        }

        String folderName = commandLine.getOptionValue("d");
        if (folderName != null) {
            parseFiles(rule, folderName);
            return;
        }









        //RuleJson rule = new MainApp().readRule();
        //testRe();

        //parseFiles(rule, "/users/luganlin/Documents/download");
        //parseFilesSync(rule, "/users/luganlin/Documents/download");
        //parseFile(rule, "C:\\Users\\lugan\\git\\law\\sourcefile\\41a2bf88-5668-47ca-abfb-3796814ed3e9.html");

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
                            if (!e.getMessage().equals("不明案由")) {
                                logger.error(String.join("\n", statements) + "\n" + file.getName(), e);
                                error.incrementAndGet();
                            }

                            total.incrementAndGet();

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

    public static RuleJson loadRule(String ruleLocation) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(ruleLocation));
        Gson gson = new Gson();
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
        /*Pattern pattern = Pattern.compile(".*(?<!日|二)$");

        Matcher matcher = pattern.matcher("审判员日");
        System.out.println(matcher.matches());*/


        //match prefix
        //（2015）宁民申177号, （2015）宁民申字第177号，匹配申
        Pattern prefixPattern = Pattern.compile("(?:诉讼费|受理费)(?:用)?(?:人民币)?([\\d|，|〇|一|二|三|四|五|六|七|八|九|十|百|千|万|亿]+)");
        Matcher prefixMatcher = prefixPattern.matcher("本案受理费100");
        if (prefixMatcher.find()) {
            System.out.println(prefixMatcher.group(1));
        }

        Pattern testPattern = Pattern.compile("(合同|同)aa$");
        Matcher testMatcher = testPattern.matcher("商业合同aa");
        if (testMatcher.find()) {
            System.out.println(testMatcher.group(1));
        }
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