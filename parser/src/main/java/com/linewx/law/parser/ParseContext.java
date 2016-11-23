package com.linewx.law.parser;

import java.util.*;

/**
 * Created by luganlin on 11/16/16.
 */
public class ParseContext {

    //parse state
    private Map<String, List<String>> results = new HashMap<>();
    private String currentState;
    private String currentStatement;
    private String preStatement;



    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getCurrentStatement() {
        return currentStatement;
    }

    public void setCurrentStatement(String currentStatement) {
        this.currentStatement = currentStatement;
    }

    public String getPreStatement() {
        return preStatement;
    }

    public void setPreStatement(String preStatement) {
        this.preStatement = preStatement;
    }


    public Map<String, List<String>> getResults() {
        return results;
    }



    public void addResult(String key, String value) {
        if (!results.containsKey(key)) {
            results.put(key, new ArrayList<>());
        }
        results.get(key).add(value);
    }

    public void removeResult(String key) {
        results.remove(key);
    }

}
