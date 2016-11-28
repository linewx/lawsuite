package com.linewx.law.instrument.task;

import com.linewx.law.instrument.audit.AuditService;
import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.instrument.model.InstrumentService;
import com.linewx.law.instrument.model.rawdata.Rawdata;
import com.linewx.law.instrument.parser.InstrumentParser;
import com.linewx.law.instrument.parser.ParserFactory;
import com.linewx.law.instrument.parser.ParserResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by luganlin on 11/26/16.
 */
public class InstrumentDBStatementsParseTask implements Callable<Boolean>{
    private List<Rawdata> statementsList;
    private InstrumentService instrumentService;
    private AuditService auditService;

    public InstrumentDBStatementsParseTask(List<Rawdata> statementsList, InstrumentService instrumentService, AuditService auditService) {
        this.statementsList = statementsList;
        this.instrumentService = instrumentService;
        this.auditService = auditService;
    }

    @Override
    public Boolean call() throws Exception {
        for (Rawdata rawdata: statementsList) {
            //ParseContext context = new ParseContext();
            List<String> statements = Arrays.asList(rawdata.getNr().split("\r\n"));
            //List<String> statements = new ArrayList<>();
            try {

                InstrumentParser parser = ParserFactory.getFromStatement(statements);
                if (parser == null) {
                    throw new InstrumentParserException(InstrumentErrorCode.UNSUPPORTED_TYPE);
                }

                ParserResult parserResult = parser.parse(statements);
                //instrument.setRawdata(rawdata.getNr());
                //instrumentService.save(instrument);
                auditService.increase();

                //instrumentService.save(instrument);

            } catch (InstrumentParserException e) {

                if (!e.getInstrumentErrorCode().equals(InstrumentErrorCode.UNSUPPORTED_TYPE)) {
                    auditService.increaseError();

                }else {
                    auditService.increaseUnsupport();
                }

            } catch (Exception e) {
                auditService.increaseError();
            }
        }
        Map<String, Long> auditResult = auditService.getResult();

        for (Map.Entry<String, Long> entry: auditResult.entrySet()) {
            System.out.print(entry.getKey() + ":" + entry.getValue() + ".");
        }
        System.out.println();
        return true;
    }
}
