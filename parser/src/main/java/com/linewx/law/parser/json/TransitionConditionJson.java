package com.linewx.law.parser.json;

/**
 * Created by luganlin on 11/17/16.
 */
public class TransitionConditionJson {
    private String previous;
    private String current;
    private String preProcessor;
    private String curProcessor;

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
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
}
