package com.linewx.law.parser.action;

import com.google.gson.JsonElement;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.cfg.ConfigurationHelper;
import com.linewx.law.parser.cfg.ParserConfiguration;


import java.util.List;
import java.util.Properties;

/**
 * Created by lugan on 11/18/2016.
 */
public abstract class AbstractActionTemplate implements ActionTemplate{
    public Boolean showAction = false;

    public void Configuration(Properties properties) {
        showAction = ConfigurationHelper.getBoolean(ParserConfiguration.SHOW_ACTION, properties);
    }

}
