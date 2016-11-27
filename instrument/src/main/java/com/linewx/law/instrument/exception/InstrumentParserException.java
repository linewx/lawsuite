package com.linewx.law.instrument.exception;

/**
 * Created by lugan on 11/23/2016.
 */
public class InstrumentParserException extends RuntimeException {
    public enum ErrorCode {
        GENERAL(1),
        UNSUPPORTED_TYPE(2),
        FIELD_MISSING(3),
        FILEDS_MISMATCH(4),
        FILED_EXCEED(5),
        INPROPER_REASON(6);

        private int errorCode;

        ErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        int getErrorCode() {
            return this.errorCode;
        }
    }



    private ErrorCode errorCode;

    public InstrumentParserException() {
        super();
        this.errorCode = ErrorCode.GENERAL;
    }

    public InstrumentParserException(String message) {
        super(message);
        this.errorCode = ErrorCode.GENERAL;
    }

    public InstrumentParserException(String message, Throwable e) {
        super(message, e);
        this.errorCode = ErrorCode.GENERAL;
    }

    public InstrumentParserException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public InstrumentParserException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public InstrumentParserException(String message, Throwable e, ErrorCode errorCode) {
        super(message, e);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
