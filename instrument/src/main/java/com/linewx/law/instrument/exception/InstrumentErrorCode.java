package com.linewx.law.instrument.exception;

public enum InstrumentErrorCode {
        GENERAL(1),
        UNSUPPORTED_TYPE(2),
        FIELD_MISSING(3),
        FILEDS_MISMATCH(4),
        FILED_EXCEED(5),
        INPROPER_REASON(6),
        UNKNOWN(7);

        private int errorCode;

        InstrumentErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return this.errorCode;
        }
    }