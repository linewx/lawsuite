package com.linewx.law.instrument.parser;

import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.parser.NameMapping;
import com.linewx.law.parser.ParseContext;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lugan on 11/28/2016.
 */
public class ParserResult {
    private ParseContext parseContext;
    private List<Pair<InstrumentErrorCode, String>> errors;
    private Instrument instrument;

    public ParserResult(ParseContext parseContext, List<Pair<InstrumentErrorCode, String>> instrumentErrorCode, Instrument instrument) {
        this.parseContext = parseContext;
        this.errors = instrumentErrorCode;
        this.instrument = instrument;
    }

    public ParseContext getParseContext() {
        return parseContext;
    }

    public void setParseContext(ParseContext parseContext) {
        this.parseContext = parseContext;
    }

    public List<Pair<InstrumentErrorCode, String>> getErrors() {
        return errors;
    }

    public void setErrors(List<Pair<InstrumentErrorCode, String>> errors) {
        this.errors = errors;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public List<String> getFullContent() {
        List<String> fullContent = new ArrayList<>();

        fullContent.add("-- original parse result --");
        List<String> parseResults = new ArrayList<>();
        Map<String, List<String>> originParseResults = parseContext.getResults();
        if (originParseResults != null) {
            for (Map.Entry<String, List<String>> oneResult : originParseResults.entrySet()) {
                List<String> valueList = oneResult.getValue();

                String values = "";
                if (valueList == null) {
                    values = "null";
                }else {
                    values = String.join("|", valueList);
                }
                parseResults.add(oneResult.getKey() + ":" + values);
            }
            fullContent.addAll(parseResults);
        }


        fullContent.add("-- generated instrument  --");
        if (instrument != null) {
            List<String> instrumentContent = new ArrayList<>();
            Class<Instrument> instrumentClazz = Instrument.class;
            for (Map.Entry<String, String> name: NameMapping.names.entrySet()) {
                try {
                    Method method = instrumentClazz.getMethod("get" + WordUtils.capitalize(name.getKey()));
                    Object oneValue = method.invoke(instrument);
                    instrumentContent.add(name.getValue() + ":" + oneValue.toString());

                }catch (Exception e) {

                }

            }
            fullContent.addAll(instrumentContent);
        }



        fullContent.add("-- errors --");
        List<String> errorContent = new ArrayList<>();
        if (errors != null) {
            for (Pair<InstrumentErrorCode, String> oneError: errors) {
                errorContent.add(oneError.getKey().name() + ":" + oneError.getValue());
            }
            fullContent.addAll(errorContent);
        }
        return fullContent;
    }
}
