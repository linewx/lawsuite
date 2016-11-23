package com.linewx.law.instrument.exception;

/**
 * Created by lugan on 11/23/2016.
 */
public class InstrumentUnsupportTypeException extends InstrumentParserException{
    private int instrumentType;


    public InstrumentUnsupportTypeException(int instrumentType) {
        super();
        this.instrumentType = instrumentType;
    }

    public InstrumentUnsupportTypeException(String message, Throwable e, int instrumentType) {
        super(message, e);
        this.instrumentType = instrumentType;
    }

    public InstrumentUnsupportTypeException(String message, int instrumentType) {
        super(message);
        this.instrumentType = instrumentType;
    }
}
