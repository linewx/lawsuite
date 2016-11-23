package com.linewx.law.instrument.exception;

/**
 * Created by lugan on 11/23/2016.
 */
public class InstrumentParserException extends RuntimeException{
    public InstrumentParserException() {
        super();
    }

    public InstrumentParserException(String message) {
        super(message);
    }

    public InstrumentParserException(String message, Throwable e) {
        super(message, e);
    }

}
