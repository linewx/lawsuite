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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by lugan on 11/23/2016.
 */
public class FinalCivilConciliationInstrumentParser extends BasicInstrumentParser implements InstrumentParser {
    private static Logger logger = LoggerFactory.getLogger(FinalCivilConciliationInstrumentParser.class);

    public FinalCivilConciliationInstrumentParser(RuleJson rule) {
        super(rule);
    }

    @Override
    void populateOtherField(ParseContext context, Instrument instrument) {
        populateDefaultCostAmount(instrument);
    }
}
