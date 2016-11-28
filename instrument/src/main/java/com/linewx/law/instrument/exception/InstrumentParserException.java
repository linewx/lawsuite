package com.linewx.law.instrument.exception;

/**
 * Created by lugan on 11/23/2016.
 */
public class InstrumentParserException extends RuntimeException {




    private InstrumentErrorCode instrumentErrorCode;

    public InstrumentParserException() {
        super();
        this.instrumentErrorCode = com.linewx.law.instrument.exception.InstrumentErrorCode.GENERAL;
    }

    public InstrumentParserException(String message) {
        super(message);
        this.instrumentErrorCode = com.linewx.law.instrument.exception.InstrumentErrorCode.GENERAL;
    }

    public InstrumentParserException(String message, Throwable e) {
        super(message, e);
        this.instrumentErrorCode = com.linewx.law.instrument.exception.InstrumentErrorCode.GENERAL;
    }

    public InstrumentParserException(InstrumentErrorCode instrumentErrorCode) {
        super();
        this.instrumentErrorCode = instrumentErrorCode;
    }

    public InstrumentParserException(String message, InstrumentErrorCode instrumentErrorCode) {
        super(message);
        this.instrumentErrorCode = instrumentErrorCode;
    }

    public InstrumentParserException(String message, Throwable e, InstrumentErrorCode instrumentErrorCode) {
        super(message, e);
        this.instrumentErrorCode = instrumentErrorCode;
    }

    public InstrumentErrorCode getInstrumentErrorCode() {
        return this.instrumentErrorCode;
    }
}
