package com.linewx.law.instrument;

/**
 * Created by luganlin on 11/23/16.
 */
public enum InstrumentTypeEnum {
    CIVIL_JUDGMENT("民事判决书");

    private String instrumentType;

    InstrumentTypeEnum(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getInstrumentType() {
        return this.instrumentType;
    }
}
