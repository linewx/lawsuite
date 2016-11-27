package com.linewx.law.instrument.parser;

import com.linewx.law.instrument.json.InstrumentRuleConverter;
import com.linewx.law.instrument.json.InstrumentRuleJson;
import com.linewx.law.parser.json.RuleJson;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luganlin on 11/27/16.
 */


public class InstrumentRuleManager {
    private static Map<String, RuleJson> rules = new HashMap<>();

    public static void add(InstrumentRuleJson instrumentRuleJson) {
        rules.put(instrumentRuleJson.getType() + instrumentRuleJson.getLevel(),
                InstrumentRuleConverter.convertInstrumentRuleToParserRule(instrumentRuleJson));
    }

    public static RuleJson lookup(String instrumentType, String instrumentLevel){
        RuleJson rule = rules.get(instrumentType + instrumentLevel);
        if (rule == null) {
            return rules.get(instrumentType);
        }
        return rule;
    }
}
