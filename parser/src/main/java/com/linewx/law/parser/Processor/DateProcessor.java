package com.linewx.law.parser.Processor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lugan on 11/18/2016.
 */
public class DateProcessor implements Processor {
    private Map<Character, String> dateMapping = new HashMap<>();


    public DateProcessor() {
        dateMapping.put('年', "-");
        dateMapping.put('月', "-");
        //dateMapping.put('日', "-");
        dateMapping.put('〇', "0");
        dateMapping.put('一', "1");
        dateMapping.put('二', "2");
        dateMapping.put('三', "3");
        dateMapping.put('四', "4");
        dateMapping.put('五', "5");
        dateMapping.put('六', "6");
        dateMapping.put('七', "7");
        dateMapping.put('八', "8");
        dateMapping.put('九', "9");

    }

    @Override
    public String getName() {
        return "date";
    }

    @Override
    public String transform(String source) {
        StringBuilder result = new StringBuilder("");
        if (source == null) {
            return "";
        }else {
            for (int i = 0; i<source.length(); i++) {
                result.append(dateMapping.getOrDefault(source.charAt(i), ""));
            }
        }
        return result.toString();
    }
}
