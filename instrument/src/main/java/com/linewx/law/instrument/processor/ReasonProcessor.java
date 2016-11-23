package com.linewx.law.instrument.processor;

import com.linewx.law.parser.Processor.Processor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lugan on 11/18/2016.
 */
public class ReasonProcessor implements Processor {
    private Map<String, String> reasons = new HashMap<>();
    private Map<String, String> reasonIndex = new HashMap<>();
    private Map<String, String> secondaryIndex = new HashMap<>();
    private Map<String, Long> ignoreValues = new HashMap<>();
    private Pattern reasonPattern;
    private Pattern secondaryReasonPattern;


    public ReasonProcessor() {
        load();
        /*List<String> reasonList = new ArrayList<>();
        reasonList.addAll(reasonIndex.keySet());
        reasonPattern = Pattern.compile(MessageFormat.format(".*([{0}])$", String.join("|", reasonList.subList(0, 100))));
        secondaryReasonPattern = Pattern.compile(MessageFormat.format(".*([{0}])$", String.join("|", reasonList.subList(0, 200))));
*/
        reasonPattern = Pattern.compile(MessageFormat.format(".*?({0})$", String.join("|", reasonIndex.keySet())));
        secondaryReasonPattern = Pattern.compile(MessageFormat.format(".*?({0})$", String.join("|", secondaryIndex.keySet())));
    }

    @Override
    public String getName() {
        return "reason";
    }

    @Override
    public String transform(String source) {
        Matcher reasonMatcher = reasonPattern.matcher(source);
        if (reasonMatcher.find()) {
            return reasonMatcher.group(1);
        }

        Matcher secondaryReasonMatcher = secondaryReasonPattern.matcher(source);
        if (secondaryReasonMatcher.find()) {
            return secondaryReasonMatcher.group(1);
        }

        return "不明案由";
    }


    public void load() {
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
