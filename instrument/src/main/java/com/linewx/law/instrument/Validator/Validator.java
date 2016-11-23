package com.linewx.law.instrument.Validator;

import com.linewx.law.parser.ParseContext;

/**
 * Created by luganlin on 11/22/16.
 */
public interface Validator {
    String getType();

    Boolean validate(ParseContext context);
}
