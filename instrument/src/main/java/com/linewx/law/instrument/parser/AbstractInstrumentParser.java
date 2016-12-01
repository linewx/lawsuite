package com.linewx.law.instrument.parser;

import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.instrument.utils.AmountParserUtil;
import com.linewx.law.instrument.utils.ContentClearUtil;
import com.linewx.law.instrument.utils.ReasonUtil;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.ParseStateMachine;
import com.linewx.law.parser.json.RuleJson;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Map;

/**
 * Created by lugan on 11/30/2016.
 */
abstract public class AbstractInstrumentParser implements InstrumentParser{
    protected  RuleJson rule;
    protected ParseStateMachine parseStateMachine;

    public AbstractInstrumentParser(RuleJson rule) {
        this.rule = rule;
        parseStateMachine = new ParseStateMachine(rule);
    }
    @Override
    public Instrument parse(List<String> statements) {
        ParseContext context = new ParseContext();
        context.setCurrentState("start");
        ParseStateMachine stateMachine = new ParseStateMachine(rule);
        stateMachine.run(context, statements);

        Instrument instrument = new Instrument();

        populateInstrument(context, instrument);
        return instrument;
    }

    abstract void populateInstrument(ParseContext context, Instrument instrument);

    public void validateField(List<String> fieldValues, String fieldName, Boolean required, Integer maxNumber) {
        if (required) {
            //validate required
            if (fieldValues == null || fieldValues.isEmpty()) {
                throw new InstrumentParserException("no " + fieldName + " found", InstrumentErrorCode.FIELD_MISSING);
            }
        }

        if (maxNumber != null) {
            if (fieldValues != null && fieldValues.size() > maxNumber) {
                throw new InstrumentParserException(maxNumber + " or more than " + fieldName + " have been found: " + fieldValues.toString(), InstrumentErrorCode.FILED_EXCEED);
            }
        }
    }
}
