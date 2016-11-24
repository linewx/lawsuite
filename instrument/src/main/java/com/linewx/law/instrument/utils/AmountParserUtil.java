package com.linewx.law.instrument.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luganlin on 11/22/16.
 */
public class AmountParserUtil {
    private static Pattern amountPattern;
    private static Map<Character, Character> numberMapping = new HashMap<>();
    static {
        amountPattern = Pattern.compile("([\\d|，|〇|一|二|三|四|五|六|七|八|九|十|百|千|万|亿]+)元");
        numberMapping.put('〇', '0');
        numberMapping.put('一', '1');
        numberMapping.put('二', '2');
        numberMapping.put('三', '3');
        numberMapping.put('四', '4');
        numberMapping.put('五', '5');
        numberMapping.put('六', '6');
        numberMapping.put('七', '7');
        numberMapping.put('八', '8');
        numberMapping.put('九', '9');
    }


    public static void main(String []argv) {
        Set<Long> numbers =  parseMainAmount("原告金额:[：, 一、被告戴兴勇应于本判决发生法律效三万元力之日起234，234元日内向原元告蒋继超给付工程款388206元；, 如果]");

        List<Long> sortedNumbers = new ArrayList(numbers);
        Collections.sort(sortedNumbers);
        Collections.reverse(sortedNumbers);

        List<Long> numberList = new ArrayList<>();
        if (sortedNumbers.size() > 3) {
            sortedNumbers.subList(0, 3);
        }

        System.out.println(sortedNumbers.stream().reduce(0L, Long::sum));


    }


    public static Long getMainAmountSum(String amountText) {
        return sumMainAmount(parseMainAmount(amountText));
    }

    private static Long sumMainAmount(Set<Long> mainAmount) {
        List<Long> sortedNumbers = new ArrayList(mainAmount);
        Collections.sort(sortedNumbers);
        Collections.reverse(sortedNumbers);

        if (sortedNumbers.size() > 3) {
            sortedNumbers.subList(0, 3);
        }

        return sortedNumbers.stream().reduce(0L, Long::sum);
    }

    private static Set<Long> parseMainAmount(String amountText) {
        Set<Long> numbers = new HashSet<>();
        Matcher matcher = amountPattern.matcher(amountText);
        while (matcher.find()) {
            numbers.add(ParseLong(matcher.group(1)));
        }
        return numbers;
    }

    //一亿六千七百万八千六十元
    public static Long ParseLong(String number) {
        if(number.contains("亿")) {
            String[] numbers = number.split("亿");
            if (numbers.length > 2 || number.length() == 0)  {
                throw new RuntimeException("unsupported number format");
            }
            
            String leftNumber = numbers[0].equals("") ? "1": numbers[0];
            String rightNumber = "0";
            if (numbers.length == 2) {
                rightNumber = numbers[1];
            }
            
            return ParseLong(leftNumber) * 100000000L + ParseLong(rightNumber);
        }

        if(number.contains("万")) {
            String[] numbers = number.split("万");
            if (numbers.length > 2 || number.length() == 0)  {
                throw new RuntimeException("unsupported number format");
            }

            String leftNumber = numbers[0].equals("") ? "1": numbers[0];
            String rightNumber = "0";
            if (numbers.length == 2) {
                rightNumber = numbers[1];
            }

            return ParseLong(leftNumber) * 10000L + ParseLong(rightNumber);
        }

        if(number.contains("千")) {
            String[] numbers = number.split("千");
            if (numbers.length > 2 || number.length() == 0)  {
                throw new RuntimeException("unsupported number format");
            }

            String leftNumber = numbers[0].equals("") ? "1": numbers[0];
            String rightNumber = "0";
            if (numbers.length == 2) {
                rightNumber = numbers[1];
            }

            return ParseLong(leftNumber) * 1000L + ParseLong(rightNumber);
        }

        if(number.contains("百")) {
            String[] numbers = number.split("百");
            if (numbers.length > 2 || number.length() == 0)  {
                throw new RuntimeException("unsupported number format");
            }

            String leftNumber = numbers[0].equals("") ? "1": numbers[0];
            String rightNumber = "0";
            if (numbers.length == 2) {
                rightNumber = numbers[1];
            }

            return ParseLong(leftNumber) * 100L + ParseLong(rightNumber);
        }

        if(number.contains("十")) {
            String[] numbers = number.split("十");
            if (numbers.length > 2 || number.length() == 0)  {
                throw new RuntimeException("unsupported number format");
            }

            String leftNumber = numbers[0].equals("") ? "1": numbers[0];
            String rightNumber = "0";
            if (numbers.length == 2) {
                rightNumber = numbers[1];
            }

            return ParseLong(leftNumber) * 10L + ParseLong(rightNumber);
        }

        if(number.contains("，")) {
            String[] numbers = number.split("，");
            if (numbers.length > 2 || number.length() == 0)  {
                throw new RuntimeException("unsupported number format");
            }

            String leftNumber = numbers[0].equals("") ? "1": numbers[0];
            String rightNumber = "0";
            if (numbers.length == 2) {
                rightNumber = numbers[1];
            }

            return ParseLong(leftNumber) * 1000L + ParseLong(rightNumber);
        }


        return transfer(number);
    }

    public static Long transfer(String originNumber) {
        StringBuilder targetNumber = new StringBuilder();

        for (int i=0; i<originNumber.length(); i++) {
            Character currentChar = originNumber.charAt(i);
            targetNumber.append(numberMapping.getOrDefault(currentChar, currentChar));
        }

        return Long.parseLong(targetNumber.toString());
    }
}
