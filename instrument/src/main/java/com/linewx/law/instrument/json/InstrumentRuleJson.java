package com.linewx.law.instrument.json;

import com.linewx.law.parser.json.StateJson;
import com.linewx.law.parser.json.TransitionJson;

import java.util.List;

/**
 * Created by luganlin on 11/17/16.
 */
public class InstrumentRuleJson {
    String firstState;

    List<StateJson> states;

    List<TransitionJson> transitions;

    String type;

    String level;

    public String getFirstState() {
        return firstState;
    }

    public void setFirstState(String firstState) {
        this.firstState = firstState;
    }

    public List<StateJson> getStates() {
        return states;
    }

    public void setStates(List<StateJson> states) {
        this.states = states;
    }

    public List<TransitionJson> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<TransitionJson> transitions) {
        this.transitions = transitions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
