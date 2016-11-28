package com.linewx.law.instrument.task;

import com.linewx.law.instrument.audit.AuditService;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.instrument.model.InstrumentService;
import com.linewx.law.instrument.parser.InstrumentParser;
import com.linewx.law.instrument.parser.ParserFactory;
import com.linewx.law.instrument.reader.InstrumentReader;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by luganlin on 11/26/16.
 */
public class InstrumentStatementsParseTask implements Callable<Boolean>{
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
            List<List<String>> statementsList = instrumentReader.readBulk(100);
            if (statementsList == null || statementsList.isEmpty()) {
                break;
            }
            for (List<String> statements: statementsList) {
                try {

                    InstrumentParser parser = ParserFactory.getFromStatement(statements);
                    if (parser == null) {
                        throw new InstrumentParserException(InstrumentParserException.ErrorCode.UNSUPPORTED_TYPE);
                    }

                    Instrument instrument = parser.parse(statements);
                    //instrumentService.save(instrument);
                    auditService.increase();
                } catch (InstrumentParserException e) {

                    if (!e.getInstrumentErrorCode().equals(InstrumentParserException.ErrorCode.UNSUPPORTED_TYPE)) {
                        auditService.increaseError();

                    }else {
                        auditService.increaseUnsupport();
                    }

                } catch (Exception e) {
                    auditService.increaseError();
                }
            }

            //System.out.println(auditService.getProcessed());
        }

       /* Map<String, Long> auditResult = auditService.getResult();

        for (Map.Entry<String, Long> entry: auditResult.entrySet()) {
            System.out.print(entry.getKey() + ":" + entry.getValue() + ".");
        }
        System.out.println();*/
        return true;
    }
}
