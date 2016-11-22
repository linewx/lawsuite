package com.linewx.law.parser.action;

import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.ProcessorHandler;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lugan on 11/18/2016.
 */
public class setFieldWithRegActionTemplate implements ActionTemplate{
    public void execute(ParseContext context, List<String> parameters) {
        if(parameters != null) {
            int length = parameters.size();
            if (length >= 3) {
                String field = parameters.get(0);
                String statementContext = parameters.get(1);
                String condition = parameters.get(2);

                String statement = "";
                Pattern pattern = Pattern.compile(condition);
                if (statementContext.equals("pre")) {
                    statement = context.getPreStatement();
                }else if(statementContext.equals("cur")) {
                    statement = context.getCurrentStatement();
                }

                if (length >= 4) {
                    String preProcessor = parameters.get(3);
                    statement = ProcessorHandler.execute(preProcessor, statement);
                }

                Matcher matcher = pattern.matcher(statement);

                if (matcher.find()) {
                    String result = matcher.group(1);
                    if (length >=5) {
                        String postProcessor = parameters.get(4);
                        result = ProcessorHandler.execute(postProcessor, result);
                    }
                    context.addResult(field, result);
                }
            }
        }

    }
}
