package com.linewx.law.parser.action;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.ProcessorHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lugan on 11/18/2016.
 */
public class setFieldWithRegActionTemplate extends AbstractActionTemplate{
    public void execute(ParseContext context, List<JsonElement> parameters) {
        if(parameters != null) {
            int length = parameters.size();
            if (length >= 3) {
                String field = parameters.get(0).getAsString();
                String statementContext = parameters.get(1).getAsString();
                JsonElement conditionElement = parameters.get(2);
                List<String> conditions = new ArrayList<>();
                if (conditionElement.isJsonPrimitive()) {
                    conditions.add(conditionElement.getAsString());
                } else if (conditionElement.isJsonArray()) {
                    JsonArray arrays = conditionElement.getAsJsonArray();
                    for (JsonElement oneCondition : arrays) {
                        conditions.add(oneCondition.getAsString());
                    }
                }

                for (String condition: conditions) {
                    String statement = "";
                    Pattern pattern = Pattern.compile(condition);
                    if (statementContext.equals("pre")) {
                        statement = context.getPreStatement();
                    }else if(statementContext.equals("cur")) {
                        statement = context.getCurrentStatement();
                    }

                    if (length >= 4) {
                        JsonElement preProcessorElement = parameters.get(3);
                        if (preProcessorElement.isJsonPrimitive()) {
                            String preProcessor = parameters.get(3).getAsString();
                            statement = ProcessorHandler.execute(preProcessor, statement);
                        }
                    }

                    Matcher matcher = pattern.matcher(statement);

                    if (matcher.find()) {
                        String result = matcher.group(1);
                        if (length >=5) {
                            JsonElement postProcessorElement = parameters.get(4);
                            if (postProcessorElement.isJsonPrimitive()) {
                                String postProcessor = parameters.get(4).getAsString();
                                result = ProcessorHandler.execute(postProcessor, result);
                            }

                        }
                        context.addResult(field, result);
                        if (showAction) {
                            System.out.println(field + ":\t" +result);
                        }
                        return;
                    }
                }

            }
        }

    }
}
