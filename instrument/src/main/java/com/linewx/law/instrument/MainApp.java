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

        //parseFiles(rule, "C:\\Users\\lugan\\git\\law\\sourcefile");
        //parseFilesSync(rule, "/users/luganlin/Documents/download");
        parseFile(rule, "C:\\Users\\lugan\\git\\law\\sourcefile\\fb4ef6d9-5717-4e0b-8b7f-73b1d69386b9.html");

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
            /*********** exception error ****************/
            System.out.println("*********** validation error ****************");
            System.out.println("-- origin content --");
            System.out.println("file name: " + file.getName());
            System.out.println(String.join("\n", statements));
            System.out.println("-- error message --");
            System.out.println(e.getMessage());
            System.out.println("*********** end validation error ************");
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
                            unsupported.incrementAndGet();
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
                            error.incrementAndGet();
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
        Pattern pattern = Pattern.compile(".*(?<!日|二)$");

        Matcher matcher = pattern.matcher("审判员日");
        System.out.println(matcher.matches());


        //match prefix
        //（2015）宁民申177号, （2015）宁民申字第177号，匹配申
        Pattern prefixPattern = Pattern.compile(".*([^字|第\\d]).*号$");
        Matcher prefixMatcher = prefixPattern.matcher("（2015）宁民申字第177号");
        if (prefixMatcher.find()) {
            System.out.println(prefixMatcher.group(1));
        }

        Pattern testPattern = Pattern.compile("(合同|同)aa$");
        Matcher testMatcher = testPattern.matcher("商业合同aa");
        if (testMatcher.find()) {
            System.out.println(testMatcher.group(1));
        }

        Pattern amountPattern = Pattern.compile("^(?:案件|本案)(?:案件)?(?:诉讼费|受理费)(?:用)?(\\d*)");
        Matcher amountMatcher = amountPattern.matcher("案件受理费1264950元，原告资产公司负担123123元，由被告化工公司负担632475元，");
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
