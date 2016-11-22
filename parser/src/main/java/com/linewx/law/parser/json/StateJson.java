package com.linewx.law.parser.json;

import java.util.List;

/**
 * Created by luganlin on 11/17/16.
 */
public class StateJson {
    private String id;
    private List<ActionJson> onEntry;
    private List<ActionJson> onStay;
    private List<ActionJson> onExit;
    private List<ActionJson> onEntryLine;
    private List<ActionJson> onExitLine;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ActionJson> getOnEntry() {
        return onEntry;
    }

    public void setOnEntry(List<ActionJson> onEntry) {
        this.onEntry = onEntry;
    }

    public List<ActionJson> getOnStay() {
        return onStay;
    }

    public void setOnStay(List<ActionJson> onStay) {
        this.onStay = onStay;
    }

    public List<ActionJson> getOnExit() {
        return onExit;
    }

    public void setOnExit(List<ActionJson> onExit) {
        this.onExit = onExit;
    }

    public List<ActionJson> getOnEntryLine() {
        return onEntryLine;
    }

    public void setOnEntryLine(List<ActionJson> onEntryLine) {
        this.onEntryLine = onEntryLine;
    }

    public List<ActionJson> getOnExitLine() {
        return onExitLine;
    }

    public void setOnExitLine(List<ActionJson> onExitLine) {
        this.onExitLine = onExitLine;
    }
}
