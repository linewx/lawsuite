package com.linewx.law.parser;

import com.google.gson.JsonElement;
import com.linewx.law.parser.action.ActionTemplate;
import com.linewx.law.parser.action.CacheMultiLinesTemplate;
import com.linewx.law.parser.action.setFieldActionTemplate;
import com.linewx.law.parser.action.setFieldWithRegActionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lugan on 11/18/2016.
 */
public class ActionHandler {
    private Map<String, ActionTemplate> actions = new HashMap<>();


    ActionHandler() {
        actions.put("setField", new setFieldActionTemplate());
        actions.put("setFieldWithReg", new setFieldWithRegActionTemplate());
        actions.put("cacheMultiLines", new CacheMultiLinesTemplate());
    }

    public void configuration(Properties properties) {
        actions.values().forEach(action -> action.Configuration(properties));
    }

    public void execute(ParseContext context, String actionName, List<JsonElement> parameters) {
        ActionTemplate action = actions.get(actionName);
        action.execute(context, parameters);
    }
}
