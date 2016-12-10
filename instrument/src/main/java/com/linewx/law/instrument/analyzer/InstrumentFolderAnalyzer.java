package com.linewx.law.instrument.analyzer;

import com.linewx.law.instrument.audit.AuditService;
import com.linewx.law.instrument.model.InstrumentService;
import com.linewx.law.instrument.model.rawdata.RawdataService;
import com.linewx.law.instrument.reader.InstrumentDBReaderV2;
import com.linewx.law.instrument.reader.InstrumentReader;
import com.linewx.law.instrument.service.LookupService;
import com.linewx.law.instrument.task.InstrumentStatementsWithMetaParseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by lugan on 12/8/2016.
 */
public class InstrumentFolderAnalyzer implements Analyzer{
    private static final Logger logger = LoggerFactory.getLogger(InstrumentFolderAnalyzer.class);

    private RawdataService rawdataService;
    private AuditService auditService;
    private InstrumentService instrumentService;
    private InstrumentReader instrumentReader;

    public InstrumentFolderAnalyzer(InstrumentReader instrumentReader) {
        this.instrumentReader = instrumentReader;
    }

    private RawdataService getRawdataService() {
        if (rawdataService == null) {
            rawdataService = LookupService.getInstance().lookup(RawdataService.class);
        }
            return rawdataService;
    }

    private AuditService getAuditService() {
        if (auditService == null) {
            auditService = LookupService.getInstance().lookup(AuditService.class);
        }
        return auditService;
    }

    private InstrumentService getInstrumentService() {
        if (instrumentService == null) {
            instrumentService = LookupService.getInstance().lookup(InstrumentService.class);
        }
        return instrumentService;
    }

    @Override
    public void analyze() {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Future> futures = new ArrayList<>();

        Long startTime = System.currentTimeMillis();
        for (int i=0; i<8; i++) {
            Future<Boolean> future = executor.submit(new InstrumentStatementsWithMetaParseTask(
                    instrumentReader,
                    getInstrumentService(), getAuditService()));
            futures.add(future);
        }

        for (Future future : futures) {
            try {
                future.get();
            }catch(Exception e) {
                logger.error(e.getMessage());
            }

        }

        Long endTime = System.currentTimeMillis();

        System.out.println((endTime - startTime) / 1000);
        Map<String, Long> auditResult = auditService.getResult();

        for (Map.Entry<String, Long> entry: auditResult.entrySet()) {
            System.out.print(entry.getKey() + ":" + entry.getValue() + ".");
        }

        Long processed = auditService.getProcessed();
        Long error = auditService.getError();
        Long unsupported = auditService.getUnsupported();
        Long ignored = auditService.getIgnored();
        Long regPer = 0L;
        if (!processed.equals(unsupported + ignored)) {
            regPer = (processed - error - unsupported - ignored) * 100 / (processed - unsupported - ignored);
        }

        System.out.print("识别率:" + regPer.toString() + "%.");
        System.out.println();
        executor.shutdown();
    }
}

