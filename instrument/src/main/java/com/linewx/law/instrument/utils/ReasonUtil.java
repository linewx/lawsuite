package com.linewx.law.instrument.utils;

import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.parser.Processor.Processor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lugan on 11/18/2016.
 */
public class ReasonUtil{
    private static Map<String, String> reasons = new HashMap<>();
    private static Map<String, String> reasonIndex = new HashMap<>();
    private static Map<String, String> secondaryIndex = new HashMap<>();
    private static Map<String, Long> ignoreValues = new HashMap<>();
    private static Pattern reasonPattern;
    private static Pattern secondaryReasonPattern;


    static {
        load();
        reasonPattern = Pattern.compile(MessageFormat.format(".*?({0})$", String.join("|", reasonIndex.keySet())));
        secondaryReasonPattern = Pattern.compile(MessageFormat.format(".*?({0})$", String.join("|", secondaryIndex.keySet())));
    }


    public static String getReasonNumber(String source) {
        if (reasonIndex.containsKey(source)) {
            return reasonIndex.get(source);
        }else {
            if (secondaryIndex.containsKey(source)) {
                return secondaryIndex.get(source);
            }else {
                throw new RuntimeException("invalid reason to identify reason number");
            }
        }
    }

    public static String getReason(String source) {
        Matcher reasonMatcher = reasonPattern.matcher(source);
        if (reasonMatcher.find()) {
            return reasonMatcher.group(1);
        }

        Matcher secondaryReasonMatcher = secondaryReasonPattern.matcher(source);
        if (secondaryReasonMatcher.find()) {
            return secondaryReasonMatcher.group(1);
        }

        return null;
    }


    private static void load() {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("reason.csv");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(is));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                // Print the content on the console
                String[] oneReason = strLine.split(",");
                String number = oneReason[0].trim();
                String name = oneReason[1].trim();

                reasons.put(number, name);
                reasonIndex.put(name, number);

                for (int i = 0; i < name.length(); i++) {
                    if (name.charAt(i) == '、') {
                        secondaryIndex.put(name.substring(i + 1), number);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


}
