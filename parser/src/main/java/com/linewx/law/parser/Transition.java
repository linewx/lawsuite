package com.linewx.law.parser;

import com.linewx.law.parser.json.TransitionJson;

import java.util.regex.Pattern;

/**
 * Created by lugan on 11/18/2016.
 */
public class Transition {
    private String source;
    private String target;
    private Pattern preStateConditionPattern;
    private Pattern curStateConditionPattern;
    private String preProcessor;



    private String curProcessor;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Pattern getPreStateConditionPattern() {
        return preStateConditionPattern;
    }

    public void setPreStateConditionPattern(Pattern preStateConditionPattern) {
        this.preStateConditionPattern = preStateConditionPattern;
    }

    public Pattern getCurStateConditionPattern() {
        return curStateConditionPattern;
    }

    public void setCurStateConditionPattern(Pattern curStateConditionPattern) {
        this.curStateConditionPattern = curStateConditionPattern;
    }

    public String getPreProcessor() {
        return preProcessor;
    }

    public void setPreProcessor(String preProcessor) {
        this.preProcessor = preProcessor;
    }

    public String getCurProcessor() {
        return curProcessor;
    }

    public void setCurProcessor(String curProcessor) {
        this.curProcessor = curProcessor;
    }

    public Transition(TransitionJson transitionJson) {
        this.source = transitionJson.getSource();
        this.target = transitionJson.getTarget();
        String preStateCondition = transitionJson.getCondition().getPrevious();
        String curStateCondition = transitionJson.getCondition().getCurrent();
        if (preStateCondition == null) {
            this.preStateConditionPattern = null;
        }else {
            this.preStateConditionPattern = Pattern.compile(preStateCondition);
        }
        if (curStateCondition == null) {
            this.curStateConditionPattern = null;
        }else {
            this.curStateConditionPattern = Pattern.compile(curStateCondition);
        }

        this.preProcessor = transitionJson.getCondition().getPreProcessor();
        this.curProcessor = transitionJson.getCondition().getCurProcessor();
    }

    public Boolean match(ParseContext context) {
        Boolean preMatch;
        Boolean curMatch;
        if (preStateConditionPattern == null) {
            preMatch = true;
        }else {
            String preStatement = context.getPreStatement();
            if (preProcessor != null) {
                preStatement = ProcessorHandler.execute(preProcessor, preStatement);
            }
            preMatch = preStateConditionPattern.matcher(preStatement).matches();
        }

        if (curStateConditionPattern == null) {
            curMatch = true;
        }else {
            String currentStatement = context.getCurrentStatement();
            if (curProcessor != null) {
                currentStatement = ProcessorHandler.execute(curProcessor, currentStatement);
            }
            curMatch = curStateConditionPattern.matcher(currentStatement).matches();
        }
        return preMatch && curMatch;
    }

}
