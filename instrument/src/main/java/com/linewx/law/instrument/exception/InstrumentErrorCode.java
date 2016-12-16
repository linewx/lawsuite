package com.linewx.law.instrument.exception;

public enum InstrumentErrorCode {
    GENERAL(1),
    UNSUPPORTED_TYPE(2),
    FIELD_MISSING(3),
    FILEDS_MISMATCH(4),
    FILED_EXCEED(5),
    INPROPER_REASON(6),
    IGNORE(7),
    UNKNOWN(8),
    SAVE_FAILED(9),
    METADATA(10);

    private int errorCode;

    InstrumentErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}