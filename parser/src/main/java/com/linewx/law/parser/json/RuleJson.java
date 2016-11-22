package com.linewx.law.parser.json;

import java.util.List;

/**
 * Created by luganlin on 11/17/16.
 */
public class RuleJson {
    String firstState;

    List<StateJson> states;

    List<TransitionJson> transitions;

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
}
