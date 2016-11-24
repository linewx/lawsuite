package com.linewx.law.parser.Processor;

/**
 * Created by lugan on 11/18/2016.
 */
public class DoubleProcessor implements Processor {
    @Override
    public String transform(String source) {
        Long number = Long.parseLong(source) * 2;
        return number.toString();
    }

    @Override
    public String getName() {
        return "double";
    }
}
