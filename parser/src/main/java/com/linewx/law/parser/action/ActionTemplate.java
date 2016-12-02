package com.linewx.law.parser.action;

import com.google.gson.JsonElement;
import com.linewx.law.parser.ParseContext;

import java.util.List;

/**
 * Created by lugan on 11/18/2016.
 */
public interface ActionTemplate {
    void execute(ParseContext context, List<JsonElement> parameters);
}
