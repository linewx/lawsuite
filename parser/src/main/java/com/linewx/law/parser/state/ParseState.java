package com.linewx.law.parser.state;

import com.linewx.law.parser.ParseContext;

/**
 * Created by luganlin on 11/16/16.
 */
public interface ParseState {
    String  getState();

    void onEntry(ParseContext context);

    void onExit(ParseContext context);

    void onStay(ParseContext context);

    void onEntryLine(ParseContext context);

    void onExitLine(ParseContext context);

    String transform(ParseContext context);

}
