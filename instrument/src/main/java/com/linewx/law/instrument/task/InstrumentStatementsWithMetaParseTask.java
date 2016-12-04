package com.linewx.law.instrument.task;

import com.linewx.law.instrument.audit.AuditService;
import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.instrument.model.InstrumentService;
import com.linewx.law.instrument.parser.InstrumentParser;
import com.linewx.law.instrument.parser.ParserFactory;
import com.linewx.law.instrument.reader.InstrumentReader;
import com.linewx.law.instrument.reader.InstrumentWithMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by luganlin on 11/26/16.
 */
public class InstrumentStatementsWithMetaParseTask implements Callable<Boolean> {
    private static Logger logger = LoggerFactory.getLogger(InstrumentStatementsWithMetaParseTask.class);
    private InstrumentReader instrumentReader;
    private InstrumentService instrumentService;
    private AuditService auditService;

    public InstrumentStatementsWithMetaParseTask(InstrumentReader instrumentReader, InstrumentService instrumentService, AuditService auditService) {
        this.instrumentReader = instrumentReader;
        this.instrumentService = instrumentService;
        this.auditService = auditService;
    }

    public void checkInstrumentLength(Instrument instrument) {
        Class<Instrument> instrumentClass = Instrument.class;
        Method[] methods = instrumentClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                try {
                    Object member = method.invoke(instrument);
                    if (member instanceof String) {
                        String methodName = method.getName();
                        if (!methodName.equals("getRawdata") && ((String) member).length() > 255) {
                            logger.error(method.getName() + "is too long");
                        }

                    }
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    public Boolean call() throws Exception {
        while (true) {
            Iterable<InstrumentWithMeta> statementsList = instrumentReader.readBulkWithMeta(100);
            if (statementsList == null || !statementsList.iterator().hasNext()) {
                break;
            }
            List<Instrument> instrumentList = new ArrayList<>();
            for (InstrumentWithMeta statementsWithMeta : statementsList) {
                Instrument instrument = new Instrument();

                //populate source info


                try {
                    List<String> statements = statementsWithMeta.getContent();
                    InstrumentParser parser = ParserFactory.getFromStatement(statements);
                    if (parser == null) {
                        throw new InstrumentParserException(InstrumentErrorCode.UNSUPPORTED_TYPE);
                    }

                    instrument = parser.parse(statements);
                    /*List<String> debugMessage = new ArrayList<>();
                    if (instrument != null) {
                        List<String> instrumentContent = new ArrayList<>();
                        Class<Instrument> instrumentClazz = Instrument.class;
                        for (Map.Entry<String, String> name : NameMapping.names.entrySet()) {
                            try {
                                Method method = instrumentClazz.getMethod("get" + WordUtils.capitalize(name.getKey()));
                                Object oneValue = method.invoke(instrument);
                                instrumentContent.add(name.getValue() + ":" + oneValue.toString());

                            } catch (Exception e) {

                            }
                            debugMessage.addAll(statements);
                            debugMessage.addAll(instrumentContent);
                            logger.info(String.join("\n", debugMessage));
                        }

                    }*/

                    instrument.setSourceType(statementsWithMeta.getSourceType());
                    instrument.setSourceId(statementsWithMeta.getSourceId());
                    instrument.setSourceName(statementsWithMeta.getSourceName());
                    instrument.setRawdata(String.join("\n", statementsWithMeta.getContent()));

                    instrumentList.add(instrument);
                    //todo: remove later, debug only
                    checkInstrumentLength(instrument);
                    auditService.increase();

                } catch (InstrumentParserException e) {

                    if (e.getInstrumentErrorCode().equals(InstrumentErrorCode.UNSUPPORTED_TYPE)) {
                        auditService.increaseUnsupport();

                    } else if (e.getInstrumentErrorCode().equals(InstrumentErrorCode.IGNORE)) {
                        auditService.increaseIgnored();
                    } else {
                        auditService.increaseError();
                    }

                    instrument.setErrorCode(e.getInstrumentErrorCode().getErrorCode());
                    instrument.setErrorMessage(e.getMessage());
                    instrument.setSourceType(statementsWithMeta.getSourceType());
                    instrument.setSourceId(statementsWithMeta.getSourceId());
                    instrument.setSourceName(statementsWithMeta.getSourceName());
                    instrument.setRawdata(String.join("\n", statementsWithMeta.getContent()));
                    instrumentList.add(instrument);
                    //todo: remove later, debug only
                    checkInstrumentLength(instrument);

                } catch (Exception e) {
                    auditService.increaseError();
                    instrument.setErrorCode(InstrumentErrorCode.UNKNOWN.getErrorCode());
                    instrument.setErrorMessage(e.getMessage());
                    instrument.setSourceType(statementsWithMeta.getSourceType());
                    instrument.setSourceId(statementsWithMeta.getSourceId());
                    instrument.setSourceName(statementsWithMeta.getSourceName());
                    instrument.setRawdata(String.join("\n", statementsWithMeta.getContent()));
                    instrumentList.add(instrument);
                    //todo: remove later, debug only
                    checkInstrumentLength(instrument);

                    /*List<String> errorMessage = new ArrayList<>();
                    errorMessage.add("");
                    errorMessage.addAll(statements);
                    errorMessage.add("error: " +  e.getMessage());
                    logger.error(String.join("\n", errorMessage));*/
                }
            }
            try {
                instrumentService.save(instrumentList);
            }catch (Exception e) {
                for (Instrument instrument:instrumentList) {
                    try {
                        instrumentService.save(instrument);
                    }catch (Exception e2) {
                        e2.printStackTrace();
                    }

                }
                e.printStackTrace();
            }

            Map<String, Long> auditResult = auditService.getResult();

            for (Map.Entry<String, Long> entry: auditResult.entrySet()) {
                System.out.print(entry.getKey() + ":" + entry.getValue() + ".");
            }
            System.out.println();
        }
        return true;
    }
}
