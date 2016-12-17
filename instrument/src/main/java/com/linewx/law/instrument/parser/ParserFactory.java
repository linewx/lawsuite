package com.linewx.law.instrument.parser;

import com.linewx.law.instrument.meta.model.InstrumentDomainEnum;
import com.linewx.law.instrument.meta.model.InstrumentLevelEnum;
import com.linewx.law.instrument.meta.model.InstrumentMetadata;
import com.linewx.law.instrument.meta.model.InstrumentTypeEnum;
import com.linewx.law.instrument.service.LookupService;
import com.linewx.law.parser.json.RuleJson;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luganlin on 11/27/16.
 */


public class ParserFactory {
    private static Pattern courtPattern;
    private static Pattern typePattern;
    private static Pattern levelPattern;
    private static Pattern anotherPattern;

    static {
        courtPattern = Pattern.compile(".*法院$");
        typePattern = Pattern.compile("(.*书)$");
        levelPattern = Pattern.compile(".*([^字|第|\\d|-]).*号.*");
        anotherPattern = Pattern.compile(".*[申|抗|再|提].*");

    }

    public static InstrumentParser get(InstrumentMetadata metadata) {
        InstrumentRuleManager instrumentRuleManager = LookupService.getInstance().lookup(InstrumentRuleManager.class);
        RuleJson rule = instrumentRuleManager.lookup(metadata);
        if (rule == null) {
            return null;
        }
        return get(metadata, rule);
    }

    public static InstrumentParser get(String instrumentType, String instrumentLevel) {
        if (instrumentType == null) {
            instrumentType = "";
        }

        if (instrumentLevel == null) {
            instrumentLevel = "";
        }
        InstrumentRuleManager instrumentRuleManager = LookupService.getInstance().lookup(InstrumentRuleManager.class);
        RuleJson ruleJson = instrumentRuleManager.lookup(instrumentType, instrumentLevel);

        if (ruleJson == null) {
            return null;
        }else {
            return get(instrumentType, instrumentLevel, ruleJson);
        }
    }

    public static InstrumentParser get(InstrumentMetadata metadata, RuleJson rule) {
        InstrumentDomainEnum domain = metadata.getInstrumentDomainEnum();
        InstrumentLevelEnum level = metadata.getInstrumentLevelEnum();
        InstrumentTypeEnum type = metadata.getInstrumentTypeEnum();


        //民事
        if (domain == InstrumentDomainEnum.CIVIL_DOMAIN) {
            //民事 判决
            if (type == InstrumentTypeEnum.ADJUDGEMENT) {
                //民事 判决 一审
                if (level == InstrumentLevelEnum.FIRST_LEVEL) {
                    return new FirstCivilJudgementInstrumentParser(rule);
                }
                //民事 判决 二审
                if (level == InstrumentLevelEnum.SECOND_LEVEL) {
                    return new FinalCivilJudgementInstrumentParser(rule);
                }

                //民事 判决 再审
                if (level == InstrumentLevelEnum.ANOTHER_LEVEL) {
                    return new AnotherCivilJudgementInstrumentParser(rule);
                }
            }

            //民事 调解
            if (type == InstrumentTypeEnum.CONCILIATION) {
                //民事 调解 一审
                if (level == InstrumentLevelEnum.FIRST_LEVEL) {
                    return new FirstCivilConciliationInstrumentParser(rule);
                }

                //民事 调解 二审
                if (level == InstrumentLevelEnum.SECOND_LEVEL) {
                    return new FinalCivilConciliationInstrumentParser(rule);
                }

                if (level == InstrumentLevelEnum.ANOTHER_LEVEL) {
                    return new AnotherCivilJudgementInstrumentParser(rule);
                }
            }
        }

        return null;
    }

    public static InstrumentParser get(String instrumentType, String instrumentLevel, RuleJson ruleJson) {
        if (instrumentType.equals("民事判决书") && instrumentLevel.equals("初")) {
            return new FirstCivilJudgementInstrumentParser(ruleJson);
        }

        if (instrumentType.equals("民事判决书") && instrumentLevel.equals("终")) {
            return new FinalCivilJudgementInstrumentParser(ruleJson);
        }

        if (instrumentType.equals("民事调解书") && instrumentLevel.equals("初")) {
            return new FirstCivilConciliationInstrumentParser(ruleJson);
        }

        if (instrumentType.equals("民事调解书") && instrumentLevel.equals("终")) {
            return new FinalCivilConciliationInstrumentParser(ruleJson);
        }

        if ((instrumentType.equals("民事判决书") || instrumentType.equals("民事调解书")) && instrumentLevel.equals("再")) {
            return new AnotherCivilJudgementInstrumentParser(ruleJson);
        }

        return null;
    }

    public static InstrumentParser getFromStatement(List<String> statements) {
        String type = null;
        String level = null;
        Boolean foundType = false;
        Boolean foundCourt = false;

        for (String statement: statements) {
            if (!foundCourt) {
                Matcher courtMatcher = courtPattern.matcher(statement);
                if (courtMatcher.matches()) {
                    foundCourt = true;
                }
            }else{
                if (!foundType) {
                    Matcher typeMatcher = typePattern.matcher(statement);
                    if (typeMatcher.find()) {
                        String originType = typeMatcher.group(1);
                        type = originType.replaceAll("[　| ]", "");
                        foundType = true;
                    }
                }else {
                    Matcher levelMatcher = levelPattern.matcher(statement);
                    if (levelMatcher.find()) {
                        Matcher anotherMatcher = anotherPattern.matcher(statement);
                        if (anotherMatcher.matches()) {
                            level = "再";
                        }else {
                            level = levelMatcher.group(1);
                        }
                        return get(type, level);
                    }
                }
            }
        }

        return null;
    }
}
