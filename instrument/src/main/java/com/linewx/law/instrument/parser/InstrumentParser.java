package com.linewx.law.instrument.parser;

import com.linewx.law.instrument.Instrument;
import com.linewx.law.instrument.InstrumentTypeEnum;
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
    private InstrumentTypeEnum type;

    public InstrumentParser(RuleJson rule, InstrumentTypeEnum type) {
        this.rule = rule;
        parseStateMachine = new ParseStateMachine(rule);
        this.type = type;
    }

    public Instrument parse(List<String> statements) {
        ParseContext context = new ParseContext();
        context.setCurrentState("start");
        ParseStateMachine stateMachine = new ParseStateMachine(rule);
        stateMachine.run(context, statements);
        Instrument instrument = new Instrument(context, type);
        instrument.loadContent();
        return instrument;
    }
}
