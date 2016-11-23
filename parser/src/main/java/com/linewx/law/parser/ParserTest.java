package com.linewx.law.parser;

import com.google.gson.Gson;
import com.linewx.law.parser.json.RuleJson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luganlin on 11/16/16.
 */
public class ParserTest {
    public static void main(String argv[]) throws Exception {
       /* System.out.println(testRe());
        return;*/

        final RuleJson rule = new ParserTest().readRule();
        //testRe();
        parseFiles(rule, "/users/luganlin/Documents/download");
        //parseFilesSync(rule, "/users/luganlin/Documents/download");
        //parseFile(rule, "C:\\Users\\lugan\\git\\law\\sourcefile\\fa0fe873-91df-4814-88b9-c58852f995c5.html").validate();
    }

    public static ParseContext parseFile (RuleJson rule, String fileName) throws Exception{
        File file = new File(fileName);
        Document doc = Jsoup.parse(file, "GBK");
        Element element = doc.getElementById("DivContent");
        Elements elements = element.children();
        List<String> statements = new ArrayList<>();
        ParseContext context = new ParseContext();
        for (Element oneElement : elements) {
            statements.add(oneElement.ownText());
            context.addResult("rawdata", oneElement.ownText());
        }

        context.setCurrentState("start");
        ParseStateMachine stateMachine = new ParseStateMachine(rule);
        stateMachine.run(context, statements);
        file.exists();
        return context;
    }


    public static void parseFiles(RuleJson rule, String folder) throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(8);
        File dir = new File(folder);

        List<Future> futures = new ArrayList<>();
        for (File file : dir.listFiles()) {
            Future<ParseContext> future = executor.submit(new Callable<ParseContext>() {
                @Override
                public ParseContext call() {
                    ParseContext context = new ParseContext();
                    try {
                        Document doc = Jsoup.parse(file, "GBK");
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
                        e.printStackTrace();
                    }

                    return context;
                }
            });

            futures.add(future);
        }

        for (Future future: futures) {
            ((ParseContext)future.get()).validate();
        }


        long endTime = System.currentTimeMillis();
        executor.shutdown();
    }


    public RuleJson readRule() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("rule1.json");
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
        if(prefixMatcher.find()) {
            System.out.println(prefixMatcher.group(1));
        }

        Pattern testPattern = Pattern.compile("(合同|同)aa$");
        Matcher testMatcher = testPattern.matcher("商业合同aa");
        if(testMatcher.find()) {
            System.out.println(testMatcher.group(1));
        }

        Pattern amountPattern = Pattern.compile("^案件受理费.*原告.*?负担(\\d*)");
        Matcher amountMatcher = amountPattern.matcher("案件受理费1264950元，原告资产公司负担123123元，由被告化工公司负担632475元，");
        if(amountMatcher.find()) {
            System.out.println(amountMatcher.group(1));
            //System.out.println(amountMatcher.group(2));
        }


    }

    public static void parseFilesSync(RuleJson rule, String folder) throws Exception{

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
                e.printStackTrace();
            }

        }
    }

    public static void testAmount() {
        //Map<Long, Long> amountFormular = new HashMap<>();
        LinkedHashMap<Long, Long> amountFormular = new LinkedHashMap<>();
        Double a = 10000L * 0.7;

    }
}
