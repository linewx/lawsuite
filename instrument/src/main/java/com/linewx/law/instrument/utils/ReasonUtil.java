package com.linewx.law.instrument.utils;

import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.parser.Processor.Processor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.*;
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

    public static void main(String ...argv) {
        //List<String> reasons = Arrays.asList("婚约财产纠纷","离婚纠纷","离婚后财产纠纷","离婚后损害责任纠纷","婚姻无效纠纷","撤销婚姻纠纷","夫妻财产约定纠纷","同居关系纠纷","抚养纠纷","扶养纠纷","赡养纠纷","收养关系纠纷","监护权纠纷","探望权纠纷","分家析产纠纷");
        //List<String> reasons = Arrays.asList("生命权、健康权、身体权纠纷","姓名权纠纷","肖像权纠纷","名誉权纠纷","荣誉权纠纷","隐私权纠纷","婚姻自主权纠纷","人身自由权纠纷","一般人格权纠纷");
        //List<String> reasons = Arrays.asList("人事争议");
        //List<String> reasons = Arrays.asList("劳动合同纠纷","社会保险纠纷","福利待遇纠纷");
        List<String> reasons = Arrays.asList("商标合同纠纷","专利合同纠纷","植物新品种合同纠纷","集成电路布图设计合同纠纷","商业秘密合同纠纷","技术合同纠纷","特许经营合同纠纷","企业名称（商号）合同纠纷","特殊标志合同纠纷","网络域名合同纠纷","知识产权质押合同纠纷","著作权权属、侵权纠纷","商标权权属、侵权纠纷","专利权权属、侵权纠纷","植物新品种权权属、侵权纠纷","集成电路布图设计专有权权属、侵权纠纷","侵害企业名称（商号）权纠纷","侵害特殊标志专有权纠纷","网络域名权属、侵权纠纷","发现权纠纷","发明权纠纷","其他科技成果权纠纷","确认不侵害知识产权纠纷","因申请知识产权临时措施损害责任纠纷","因恶意提起知识产权诉讼损害责任纠纷","专利权宣告无效后返还费用纠纷");
        for (String oneReason : reasons) {
            System.out.println(oneReason + ":" + getReasonNumber(oneReason));
        }
    }
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
            }else if(!source.endsWith("纠纷")) {
                return getReasonNumber(source + "纠纷");
            }
            else {
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
                    new InputStreamReader(is, "UTF8"));
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
