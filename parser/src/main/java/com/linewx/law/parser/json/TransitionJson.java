package com.linewx.law.parser.json;

/**
 * Created by luganlin on 11/17/16.
 */
public class TransitionJson {
    private String source;
    private String target;
    private TransitionConditionJson condition;

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

    public TransitionConditionJson getCondition() {
        return condition;
    }

    public void setCondition(TransitionConditionJson condition) {
        this.condition = condition;
    }
}
