package com.linewx.law.instrument.parser;

import com.linewx.law.instrument.Instrument;
import com.linewx.law.instrument.Validator.ValidationResult;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.ParseStateMachine;
import com.linewx.law.parser.json.RuleJson;
import org.jsoup.nodes.Element;

import java.util.List;

/**
 * Created by lugan on 11/23/2016.
 */
public class InstrumentParser {
    private ParseStateMachine parseStateMachine;
    private RuleJson rule;

    public InstrumentParser(RuleJson rule) {
        this.rule = rule;
        parseStateMachine = new ParseStateMachine(rule);
    }

    Instrument parse(List<String> statements) {
        ParseContext context = new ParseContext();
        context.setCurrentState("start");
        ParseStateMachine stateMachine = new ParseStateMachine(rule);
        stateMachine.run(context, statements);
        Instrument instrument = new Instrument(context);
        return instrument;
    }
}
