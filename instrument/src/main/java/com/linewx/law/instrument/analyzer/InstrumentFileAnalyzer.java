package com.linewx.law.instrument.analyzer;

import com.linewx.law.instrument.audit.AuditService;
import com.linewx.law.instrument.model.InstrumentService;
import com.linewx.law.instrument.model.rawdata.RawdataService;
import com.linewx.law.instrument.reader.InstrumentDBReaderV2;
import com.linewx.law.instrument.service.LookupService;
import com.linewx.law.instrument.task.InstrumentFileParseTask;
import com.linewx.law.instrument.task.InstrumentStatementsWithMetaParseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
public class InstrumentFileAnalyzer implements Analyzer{
    private static final Logger logger = LoggerFactory.getLogger(InstrumentFileAnalyzer.class);

    private RawdataService rawdataService;
    private AuditService auditService;
    private InstrumentService instrumentService;

    private String fileName;

    public InstrumentFileAnalyzer(String fileName) {
        this.fileName = fileName;
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
        File file = new File(fileName);
        InstrumentFileParseTask task = new InstrumentFileParseTask(file, getInstrumentService(), getAuditService());
        try {
            task.call();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}

