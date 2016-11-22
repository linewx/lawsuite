package com.linewx.law.parser.json;

import java.util.List;

/**
 * Created by luganlin on 11/17/16.
 */
public class ActionJson {
    private String action;
    private List<String> parameters;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
}
