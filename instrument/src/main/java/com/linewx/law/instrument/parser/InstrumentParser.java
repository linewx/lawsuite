package com.linewx.law.instrument.parser;

import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.parser.json.RuleJson;

import java.util.List;

/**
 * Created by luganlin on 11/27/16.
 */
public interface InstrumentParser {

    ParserResult parse(List<String> statements);
}
