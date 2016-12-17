package com.linewx.law.instrument.parser;

import com.google.gson.Gson;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.json.InstrumentRuleConverter;
import com.linewx.law.instrument.json.InstrumentRuleJson;
import com.linewx.law.instrument.meta.model.InstrumentMetadata;
import com.linewx.law.parser.json.RuleJson;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luganlin on 11/27/16.
 */

@Component
public class InstrumentRuleManager {
    private Map<String, RuleJson> rules = new HashMap<>();
    private RuleJson metaRule;

    @PostConstruct
    public void load() {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] metaInfResources = resourcePatternResolver.getResources("classpath*:rule/*Rule.json");
            Gson gson = new Gson();
            for(Resource rule : metaInfResources){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(rule.getInputStream(), "UTF8"));
                InstrumentRuleJson instrumentRuleJson = gson.fromJson(bufferedReader, InstrumentRuleJson.class);
                add(instrumentRuleJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(InstrumentRuleJson instrumentRuleJson) {
        if (instrumentRuleJson.getDomain().equals("meta")) {
            metaRule = InstrumentRuleConverter.convertInstrumentRuleToParserRule(instrumentRuleJson);
            return;
        }

        rules.put(String.join("-", instrumentRuleJson.getDomain(), instrumentRuleJson.getLevel(), instrumentRuleJson.getType()),
                InstrumentRuleConverter.convertInstrumentRuleToParserRule(instrumentRuleJson));
    }

    public RuleJson getMetaRule() {
        return metaRule;
    }

    public RuleJson lookup(InstrumentMetadata metadata) {
        for (String oneRule: rules.keySet()) {
            List<String> ruleKey = Arrays.asList(oneRule.split("-"));
            if (ruleKey.size() != 3) {
                throw new InstrumentParserException("invalid rule");
            }

            String ruleDomainKey = ruleKey.get(0);
            String ruleLevelKey = ruleKey.get(1);
            String ruleTypeKey = ruleKey.get(2);

            if (ruleDomainKey.contains(metadata.getInstrumentDomainEnum().getDomain()) &&
                    ruleLevelKey.contains(metadata.getInstrumentLevelEnum().getLevel()) &&
                    ruleTypeKey.contains(metadata.getInstrumentTypeEnum().getType())) {
                return rules.get(oneRule);
            }
        }
        return null;
    }

    public RuleJson lookup(String instrumentType, String instrumentLevel){
        RuleJson rule = rules.get(instrumentType + "-"  + instrumentLevel);
        if (rule == null) {
            for (String oneRule: rules.keySet()) {
                List<String> ruleKey = Arrays.asList(oneRule.split("-"));
                if (ruleKey.isEmpty()) {
                    return null;
                } if (ruleKey.size() == 1) {
                    if (ruleKey.get(0).contains(instrumentType)) {
                        return rules.get(oneRule);
                    }
                } else if (ruleKey.size() == 2) {
                    if (ruleKey.get(0).contains(instrumentType) && ruleKey.get(1).contains(instrumentLevel)) {
                        return rules.get(oneRule);
                    }
                }else {
                    return null;
                }
            }
            //return rules.get(instrumentType);
        }
        return rule;
    }
}
