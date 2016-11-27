package com.linewx.law.instrument.json;

import com.linewx.law.parser.json.RuleJson;

/**
 * Created by luganlin on 11/27/16.
 */
public class InstrumentRuleConverter {
    public static RuleJson convertInstrumentRuleToParserRule(InstrumentRuleJson instrumentRuleJson) {
        RuleJson ruleJson = new RuleJson();
        ruleJson.setFirstState(instrumentRuleJson.getFirstState());
        ruleJson.setStates(instrumentRuleJson.getStates());
        ruleJson.setTransitions(instrumentRuleJson.getTransitions());
        return ruleJson;
    }
}
