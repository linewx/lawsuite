package com.linewx.law.parser.action;

import com.linewx.law.parser.ParseContext;

import java.lang.reflect.AccessibleObject;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luganlin on 11/22/16.
 */
public class CacheMultiLinesTemplate implements ActionTemplate{
    @Override
    public void execute(ParseContext context, List<String> parameters) {
        if(parameters == null || parameters.size()!=3) {
            return;
        }else {
            //not support embed condition

            String cacheName = parameters.get(0);
            String cacheFlag = cacheName + "Flag";

            String startCondition = parameters.get(1);
            String endCondition = parameters.get(2);
            Pattern startPattern = Pattern.compile(startCondition);
            Pattern endParttern = Pattern.compile(endCondition);

            if (context.getResults().get(cacheFlag) == null) {
                //not cache yet
                Matcher startMatcher = startPattern.matcher(context.getCurrentStatement());
                if  (startMatcher.find()) {
                    //start caching
                    context.addResult(cacheName, startMatcher.group(1));
                    context.addResult(cacheFlag, "true");
                }
            }else {
                Matcher endMatcher = endParttern.matcher(context.getCurrentStatement());
                if (endMatcher.find()) {
                    //stop caching
                    context.addResult(cacheName, endMatcher.group(1));
                    context.removeResult(cacheFlag);
                }else {
                    //keep caching
                    context.addResult(cacheName, context.getCurrentStatement());
                }
            }

        }


    }
}
