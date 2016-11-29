package com.linewx.law.instrument.utils;

/**
 * Created by lugan on 11/29/2016.
 */
public class ContentClearUtil {
    public static String clearAbstract(String content) {
        if (content == null) {
            return content;
        }

        int index = content.indexOf("概要基本信息");
        if (index == -1) {
            return content;
        }else {
            return content.substring(0, index);
        }
    }
}
