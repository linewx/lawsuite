package com.linewx.law.instrument.utils;

import com.linewx.law.parser.Processor.Processor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lugan on 11/18/2016.
 */
public class LevelUtil{
    private static Map<String, Integer> levelMapping = new HashMap<>();


    static {
        levelMapping.put("初", 1);
        levelMapping.put("终", 2);
    }

    public static Integer getLevel(String source) {
        return levelMapping.getOrDefault(source, 3);
    }
}
