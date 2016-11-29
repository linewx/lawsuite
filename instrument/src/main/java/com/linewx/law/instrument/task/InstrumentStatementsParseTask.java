package com.linewx.law.instrument.task;

import com.linewx.law.instrument.audit.AuditService;
import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.instrument.model.InstrumentService;
import com.linewx.law.instrument.parser.InstrumentParser;
import com.linewx.law.instrument.parser.ParserFactory;
import com.linewx.law.instrument.parser.ParserResult;
import com.linewx.law.instrument.reader.InstrumentReader;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by luganlin on 11/26/16.
 */
public class InstrumentStatementsParseTask implements Callable<Boolean>{
    private static Logger logger = LoggerFactory.getLogger(InstrumentStatementsParseTask.class);
    private InstrumentReader instrumentReader;
    private InstrumentService instrumentService;
    private AuditService auditService;

    public InstrumentStatementsParseTask(InstrumentReader instrumentReader, InstrumentService instrumentService, AuditService auditService) {
        this.instrumentReader = instrumentReader;
        this.instrumentService = instrumentService;
        this.auditService = auditService;
    }

    @Override
    public Boolean call() throws Exception {
        while(true) {
            Instrument instrument = null;
            List<List<String>> statementsList = instrumentReader.readBulk(100);
            if (statementsList == null || statementsList.isEmpty()) {
                break;
            }
            for (List<String> statements: statementsList) {
                try {

                    InstrumentParser parser = ParserFactory.getFromStatement(statements);
                    if (parser == null) {
                        throw new InstrumentParserException(InstrumentErrorCode.UNSUPPORTED_TYPE);
                    }

                    instrument = parser.parse(statements);

                    //instrumentService.save(instrument);
                    auditService.increase();

                } catch (InstrumentParserException e) {

                    if (!e.getInstrumentErrorCode().equals(InstrumentErrorCode.UNSUPPORTED_TYPE)) {
                        auditService.increaseError();
                        List<String> errorMessage = new ArrayList<>();
                        errorMessage.add("");
                        errorMessage.addAll(statements);
                        errorMessage.add("error: " + e.getInstrumentErrorCode().name() + "-" + e.getMessage());
                        logger.error(String.join("\n", errorMessage));

                    }else {

                        auditService.increaseUnsupport();

                    }

                } catch (Exception e) {
                    auditService.increaseError();
                    List<String> errorMessage = new ArrayList<>();
                    errorMessage.add("");
                    errorMessage.addAll(statements);
                    errorMessage.add("error: " +  e.getMessage());
                    logger.error(String.join("\n", errorMessage));
                }
            }


        }


        return true;
    }
}
