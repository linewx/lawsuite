package com.linewx.law.instrument.meta;

import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.meta.identifier.domain.DomainIdentifier;
import com.linewx.law.instrument.meta.identifier.level.*;
import com.linewx.law.instrument.meta.identifier.type.TypeIdentifier;
import com.linewx.law.instrument.meta.model.InstrumentDomainEnum;
import com.linewx.law.instrument.meta.model.InstrumentLevelEnum;
import com.linewx.law.instrument.meta.model.InstrumentMetadata;
import com.linewx.law.instrument.meta.model.InstrumentTypeEnum;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.ParseStateMachine;
import com.linewx.law.parser.cfg.ParserConfiguration;
import com.linewx.law.parser.json.RuleJson;

import java.util.List;
import java.util.Properties;

/**
 * Created by lugan on 12/16/2016.
 */
public class MetaInstrumentParser {
    private RuleJson rule;

    public MetaInstrumentParser(RuleJson rule) {
        this.rule = rule;
    }

    public InstrumentMetadata parse(List<String> statements, Boolean debugMode) {
        ParseContext context = new ParseContext();
        context.setCurrentState("start");
        Properties properties = new Properties();
        if (debugMode) {
            properties.put(ParserConfiguration.SHOW_ACTION, true);
            properties.put(ParserConfiguration.SHOW_TRANSITION, true);
        }

        ParseStateMachine stateMachine = new ParseStateMachine(rule, properties);
        stateMachine.run(context, statements);
        return getMetadata(context);
    }

    private InstrumentMetadata getMetadata(ParseContext context) {
        MetaParseContext metaParseContext = new MetaParseContext(context);

        InstrumentDomainEnum instrumentDomainEnum;
        InstrumentTypeEnum instrumentTypeEnum;
        InstrumentLevelEnum instrumentLevelEnum;

        instrumentDomainEnum = DomainIdentifier.getDomain(metaParseContext);

        LevelIdentifier levelIdentifier;
        switch (instrumentDomainEnum) {
            case CIVIL_DOMAIN:
                levelIdentifier = new CivilLevelIdentifier();
                break;
            case CRIMINAL_DOMAIN:
                levelIdentifier = new CriminalLevelIdentifier();
                break;
            case ADMINISTRATION_DOMAIN:
                levelIdentifier = new AdministrationLevelIdentifier();
                break;
            case COMPENSATION_DOMAIN:
                levelIdentifier = new CompensationLevelIdentifier();
                break;
            case EXECUTION_DOMAIN:
                levelIdentifier = new ExecutionLevelIdentifier();
                break;
            default:
                throw new InstrumentParserException("unknown domain", InstrumentErrorCode.METADATA);
        }

        instrumentLevelEnum = levelIdentifier.identify(metaParseContext);
        instrumentTypeEnum = TypeIdentifier.identify(metaParseContext);


        InstrumentMetadata instrumentMetadata = new InstrumentMetadata();
        instrumentMetadata.setInstrumentDomainEnum(instrumentDomainEnum);
        instrumentMetadata.setInstrumentLevelEnum(instrumentLevelEnum);
        instrumentMetadata.setInstrumentTypeEnum(instrumentTypeEnum);

        return instrumentMetadata;
    }

}
