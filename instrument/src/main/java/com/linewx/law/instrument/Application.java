package com.linewx.law.instrument;

import com.google.gson.Gson;
import com.linewx.law.instrument.audit.AuditService;
import com.linewx.law.instrument.json.InstrumentRuleJson;
import com.linewx.law.instrument.model.InstrumentService;
import com.linewx.law.instrument.model.rawdata.Rawdata;
import com.linewx.law.instrument.model.rawdata.RawdataService;
import com.linewx.law.instrument.parser.InstrumentRuleManager;
import com.linewx.law.instrument.reader.InstrumentFilesReader;
import com.linewx.law.instrument.reader.InstrumentReader;
import com.linewx.law.instrument.task.InstrumentFileParseTask;
import com.linewx.law.instrument.task.InstrumentDBStatementsParseTask;
import com.linewx.law.instrument.task.InstrumentStatementsParseTask;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by luganlin on 11/22/16.
 */
@SpringBootApplication
public class Application implements CommandLineRunner{
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    InstrumentService instrumentService;

    @Autowired
    AuditService auditService;

    @Autowired
    RawdataService rawdataService;

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
        ruleOption.setRequired(false);
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

        String fileName = commandLine.getOptionValue("f");
        if (fileName != null) {
            parseFile(fileName);
            return;
        }

        String folderName = commandLine.getOptionValue("d");
        if (folderName != null) {
            parseFiles(folderName);
            return;
        }

        //parseFromDB();
        //parseFromDBSync();
        //storeDataToFile();

        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Future> futures = new ArrayList<>();

        Long startTime = System.currentTimeMillis();
        InstrumentReader instrumentReader = new InstrumentFilesReader("C:\\Users\\lugan\\Downloads\\lawtest");
        for (int i=0; i<8; i++) {
            Future<Boolean> future = executor.submit(new InstrumentStatementsParseTask(
                    instrumentReader,
                    instrumentService, auditService));
            futures.add(future);
        }

        for (Future future : futures) {
            future.get();
        }

        Long endTime = System.currentTimeMillis();

        System.out.println((endTime - startTime) * 1000);
        Map<String, Long> auditResult = auditService.getResult();

        for (Map.Entry<String, Long> entry: auditResult.entrySet()) {
            System.out.print(entry.getKey() + ":" + entry.getValue() + ".");
        }

        Long processed = auditService.getProcessed();
        Long error = auditService.getError();
        Long unsupported = auditService.getUnsupported();
        Long regPer = (processed - error - unsupported) * 100 / (processed - unsupported);

        System.out.print("识别率:" + regPer.toString() + "%.");
        System.out.println();
        executor.shutdown();

    }

    public void parseFile(String fileName) {
        File file = new File(fileName);
        InstrumentFileParseTask task = new InstrumentFileParseTask(file, instrumentService, auditService);
        try {
            task.call();
        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void parseFromDBSync() throws Exception {
        List<Rawdata> rawdatas = rawdataService.getData(1);
        InstrumentDBStatementsParseTask instrumentStatementsParseTask =
                new InstrumentDBStatementsParseTask(rawdatas, instrumentService, auditService);
        instrumentStatementsParseTask.call();
    }

    public void storeDataToFile() throws Exception{
        int currentPage = 0;
        int process = 0;
        while(true) {
            List<Rawdata> rawdatas = rawdataService.getData(currentPage);
            if (rawdatas == null || rawdatas.isEmpty()) {
                break;
            }else {
                for (Rawdata rawdata: rawdatas) {
                    String id = ((Long)rawdata.getId()).toString();
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream("C:\\Users\\lugan\\Downloads\\lawsource2\\" + id + ".txt"), "utf-8"))) {
                        writer.write(rawdata.getNr());
                    }
                }
            }

            process = process + 100;
            System.out.println("processed" + process);
            currentPage = currentPage + 1;

        }


    }

    public void parseFromDB() throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Future> futures = new ArrayList<>();

        int currentPage = 0;
        while(true) {
            List<Rawdata> rawdatas = rawdataService.getData(currentPage);
            if (rawdatas == null || rawdatas.isEmpty()) {
                break;
            }else {
                Future<Boolean> future = executor.submit(new InstrumentDBStatementsParseTask(rawdatas, instrumentService, auditService));
                futures.add(future);
            }

            currentPage = currentPage + 1;

        }

        for (Future future : futures) {
            future.get();
        }

        Map<String, Long> auditResult = auditService.getResult();

        for (Map.Entry<String, Long> entry: auditResult.entrySet()) {
            System.out.print(entry.getKey() + ":" + entry.getValue() + ".");
        }

        Long processed = auditService.getProcessed();
        Long error = auditService.getError();
        Long unsupported = auditService.getUnsupported();
        Long regPer = (processed - error - unsupported) * 100 / (processed - unsupported);

        System.out.print("识别率:" + regPer.toString() + "%.");
        System.out.println();
        executor.shutdown();

    }
    public void parseFiles(String folder) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(8);
        File dir = new File(folder);

        List<Future> futures = new ArrayList<>();
        for (File file : dir.listFiles()) {
            Future<Boolean> future = executor.submit(new InstrumentFileParseTask(file, instrumentService, auditService));
            futures.add(future);
        }

        for (Future future : futures) {
            future.get();
        }

        Map<String, Long> auditResult = auditService.getResult();

        for (Map.Entry<String, Long> entry: auditResult.entrySet()) {
            System.out.print(entry.getKey() + ":" + entry.getValue() + ".");
        }

        Long processed = auditService.getProcessed();
        Long error = auditService.getError();
        Long unsupported = auditService.getUnsupported();
        Long regPer = (processed - error - unsupported) * 100 / (processed - unsupported);

        System.out.print("识别率:" + regPer.toString() + "%.");
        System.out.println();
        executor.shutdown();
    }


    public static InstrumentRuleJson loadRule(String ruleLocation) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(ruleLocation));
        Gson gson = new Gson();
        InstrumentRuleJson rule = gson.fromJson(bufferedReader, InstrumentRuleJson.class);
        return rule;
    }

    /*public static void loadRule() {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("firstCivilAjudgementRule.json");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(is, "UTF8"));
            Gson gson = new Gson();
            InstrumentRuleJson rule = gson.fromJson(bufferedReader, InstrumentRuleJson.class);
            InstrumentRuleManager.add(rule);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }*/

}
