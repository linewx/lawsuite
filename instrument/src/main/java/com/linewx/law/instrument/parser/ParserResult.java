package com.linewx.law.instrument.parser;

import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.parser.ParseContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by lugan on 11/28/2016.
 */
public class ParserResult {
    private ParseContext parseContext;
    private List<Pair<InstrumentErrorCode, String>> instrumentErrorCode;
    private Instrument instrument;

    public ParserResult(ParseContext parseContext, List<Pair<InstrumentErrorCode, String>> instrumentErrorCode, Instrument instrument) {
        this.parseContext = parseContext;
        this.instrumentErrorCode = instrumentErrorCode;
        this.instrument = instrument;
    }

    public ParseContext getParseContext() {
        return parseContext;
    }

    public void setParseContext(ParseContext parseContext) {
        this.parseContext = parseContext;
    }

    public List<Pair<InstrumentErrorCode, String>> getInstrumentErrorCode() {
        return instrumentErrorCode;
    }

    public void setInstrumentErrorCode(List<Pair<InstrumentErrorCode, String>> instrumentErrorCode) {
        this.instrumentErrorCode = instrumentErrorCode;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }
}
