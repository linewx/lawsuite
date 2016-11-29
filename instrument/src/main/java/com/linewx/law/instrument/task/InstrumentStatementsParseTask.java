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
            ParserResult parserResult = null;
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

                    parserResult = parser.parse(statements);

                    List<Pair<InstrumentErrorCode, String>> errors = parserResult.getErrors();
                    List<String> debugContent = new ArrayList<>();
                    if(parserResult != null) {
                        debugContent.add("########### debug content ##########");
                        debugContent.addAll(statements);
                        debugContent.addAll(parserResult.getFullContent());
                        debugContent.add("########### end debug content #########");
                        logger.error(String.join("\r\n", debugContent));
                    }

                    //instrumentService.save(instrument);
                    auditService.increase();

                } catch (InstrumentParserException e) {

                    if (!e.getInstrumentErrorCode().equals(InstrumentErrorCode.UNSUPPORTED_TYPE)) {
                        auditService.increaseError();

                    }else {
                        auditService.increaseUnsupport();
                    }

                    List<String> debugContent = new ArrayList<>();
                    if(parserResult != null) {
                        debugContent.add("########### debug content ##########");
                        debugContent.addAll(statements);
                        debugContent.addAll(parserResult.getFullContent());
                        debugContent.add("########### end debug content #########");
                        logger.error(String.join("\r\n", debugContent));
                    }

                } catch (Exception e) {
                    auditService.increaseError();
                    List<String> debugContent = new ArrayList<>();
                    if(parserResult != null) {
                        debugContent.add("########### debug content ##########");
                        debugContent.addAll(statements);
                        debugContent.addAll(parserResult.getFullContent());
                        debugContent.add("########### end debug content #########");
                        logger.error(String.join("\r\n", debugContent));
                    }
                }
            }


        }


        return true;
    }
}
