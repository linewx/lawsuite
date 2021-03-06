package com.linewx.law.instrument.task;

import com.linewx.law.instrument.audit.AuditService;
import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.meta.MetaInstrumentParser;
import com.linewx.law.instrument.meta.model.InstrumentMetadata;
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

   /* public void checkInstrumentLength(Instrument instrument) {
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
    }*/
   private void populateCommonInfo(Instrument instrument, InstrumentWithMeta instrumentWithMeta) {
       instrument.setSourceId(instrumentWithMeta.getSourceId());
       instrument.setSourceName(instrumentWithMeta.getSourceName());
       instrument.setSourceType(instrumentWithMeta.getSourceType());
   }

   private void populateErrorInfo(Instrument instrument,
                                 InstrumentErrorCode instrumentErrorCode,
                                 String errorMessage) {
       instrument.setErrorCode(instrumentErrorCode.getErrorCode());
       instrument.setErrorMessage(errorMessage);

   }

   private void populateMetaInfo(Instrument instrument, InstrumentMetadata metadata) {
       instrument.setDomainTag(metadata.getInstrumentDomainEnum().getDomain());
       instrument.setLevelTag(metadata.getInstrumentLevelEnum().getLevel());
       instrument.setTypeTag(metadata.getInstrumentTypeEnum().getType());
   }

    @Override
    public Boolean call() throws Exception {
        while (true) {
            Iterable<InstrumentWithMeta> statementsList = instrumentReader.readBulkWithMeta(100);
            if (statementsList == null || !statementsList.iterator().hasNext()) {
                break;
            }
            List<Instrument> instrumentList = new ArrayList<>();
            try {
                for (InstrumentWithMeta statementsWithMeta : statementsList) {

                    Instrument instrument = new Instrument();
                    InstrumentMetadata instrumentMetadata = new InstrumentMetadata();
                    //populate source info
                    try {
                        List<String> statements = statementsWithMeta.getContent();
                        instrumentMetadata = MetaInstrumentParser.parse(statements, false);
                        InstrumentParser parser = ParserFactory.get(instrumentMetadata);
                        //InstrumentParser parser = ParserFactory.getFromStatement(statements);
                        if (parser == null) {
                            throw new InstrumentParserException(InstrumentErrorCode.UNSUPPORTED_TYPE);
                        }

                        instrument = parser.parse(statements, false);
                        populateCommonInfo(instrument, statementsWithMeta);
                        populateMetaInfo(instrument, instrumentMetadata);
                        instrumentList.add(instrument);
                        auditService.increase();

                    } catch (InstrumentParserException e) {
                        if (e.getInstrumentErrorCode().equals(InstrumentErrorCode.UNSUPPORTED_TYPE)) {
                            auditService.increaseUnsupport();

                        } else if (e.getInstrumentErrorCode().equals(InstrumentErrorCode.IGNORE)) {
                            auditService.increaseIgnored();
                        } else if (e.getInstrumentErrorCode().equals(InstrumentErrorCode.METADATA)) {
                            auditService.increaseIgnored();
                        }else {
                            auditService.increaseError();
                        }

                        populateMetaInfo(instrument, instrumentMetadata);
                        populateCommonInfo(instrument, statementsWithMeta);
                        populateErrorInfo(instrument, e.getInstrumentErrorCode(), e.getMessage());
                        instrumentList.add(instrument);
                    } catch (Exception e) {
                        auditService.increaseError();
                        populateCommonInfo(instrument, statementsWithMeta);
                        populateErrorInfo(instrument, InstrumentErrorCode.UNKNOWN, e.getMessage());
                        instrumentList.add(instrument);
                        //todo: remove later, debug only
                    }
                }
                try {
                    instrumentService.save(instrumentList);
                } catch (Exception e) {
                    for (Instrument instrument : instrumentList) {
                        try {
                            instrumentService.save(instrument);
                        } catch (Exception e2) {
                            Long sourceId = instrument.getSourceId();
                            if (sourceId != null) {
                                logger.error("can not save instrument parse result:\n" + sourceId.toString());
                                Instrument errorInstrument = new Instrument();
                                errorInstrument.setSourceId(sourceId);
                                errorInstrument.setErrorCode(InstrumentErrorCode.SAVE_FAILED.getErrorCode());
                                errorInstrument.setErrorMessage("can not save record");
                                instrumentService.save(errorInstrument);
                            }
                            logger.error("save one record failed", e2);
                        }
                    }
                    logger.error("save batch failed", e);
                }
            } catch (Exception e) {
                logger.error("unknown save failed", e);
            }


            Map<String, Long> auditResult = auditService.getResult();

            for (Map.Entry<String, Long> entry : auditResult.entrySet()) {
                System.out.print(entry.getKey() + ":" + entry.getValue() + ".");
            }
            System.out.println();
        }
        return true;
    }
}
