package com.linewx.law.instrument.processor;

import com.linewx.law.parser.Processor.Processor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lugan on 11/18/2016.
 */
public class LevelProcessor implements Processor {
    private Map<String, String> levelMapping = new HashMap<>();


    public LevelProcessor() {
        levelMapping.put("初", "1");
        levelMapping.put("终", "2");

    }

    @Override
    public String getName() {
        return "level";
    }

    @Override
    public String transform(String source) {
        return levelMapping.getOrDefault(source, "3");
    }
}
