package com.linewx.law.parser.Processor;

/**
 * Created by lugan on 11/18/2016.
 */
public class EraseSpaceProcessor implements Processor {
    @Override
    public String transform(String source) {
        return source.replaceAll("[　| ]", "");
    }

    @Override
    public String getName() {
        return "eraseSpace";
    }
}
