package com.linewx.law.parser.cfg;

import java.util.Properties;

/**
 * Created by luganlin on 12/11/16.
 */
public class ConfigurationHelper {
    public static Boolean getBoolean(String name, Properties properties) {
        if (properties == null) {
            return false;
        }

        Object value = properties.get(name);

        if (value == null) {
            return false;
        }

        if (Boolean.class.isInstance(value)) {
            return (Boolean)value;
        }else if (String.class.isInstance(value)) {
            return Boolean.parseBoolean((String)value);
        }else {
            throw new RuntimeException("unknown boolean value:" + name);
        }
    }
}
