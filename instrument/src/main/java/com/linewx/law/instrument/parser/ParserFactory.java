package com.linewx.law.instrument.parser;

import com.linewx.law.parser.json.RuleJson;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luganlin on 11/27/16.
 */

public class ParserFactory {
    private static Pattern courtPattern;
    private static Pattern typePattern;
    private static Pattern levelPattern;

    static {
        courtPattern = Pattern.compile(".*法院$");
        typePattern = Pattern.compile("(.*书)$");
        levelPattern = Pattern.compile(".*([^字|第|\\d|-]).*号.*");
    }

    public static InstrumentParser get(String instrumentType, String instrumentLevel) {
        if (instrumentType == null) {
            instrumentType = "";
        }

        if (instrumentLevel == null) {
            instrumentLevel = "";
        }
        RuleJson ruleJson = InstrumentRuleManager.lookup(instrumentType, instrumentLevel);

        if (ruleJson == null) {
            return null;
        }else {
            return get(instrumentType, instrumentLevel, ruleJson);
        }
    }

    public static InstrumentParser get(String instrumentType, String instrumentLevel, RuleJson ruleJson) {
        if ((instrumentType + instrumentLevel).equals("民事判决书初")) {
            return new CivilJudgementInstrumentParser(ruleJson);
        }else {
            return null;
        }
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
                        level = levelMatcher.group(1);
                        return get(type, level);
                    }
                }
            }
        }

        return null;
    }
}