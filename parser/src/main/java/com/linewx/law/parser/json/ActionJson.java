package com.linewx.law.parser.json;

import com.google.gson.JsonElement;

import java.util.List;

/**
 * Created by luganlin on 11/17/16.
 */
public class ActionJson {
    private String action;
    private List<JsonElement> parameters;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<JsonElement> getParameters() {
        return parameters;
    }

    public void setParameters(List<JsonElement> parameters) {
        this.parameters = parameters;
    }
}
