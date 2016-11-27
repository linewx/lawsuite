package com.linewx.law.instrument;

/**
 * Created by luganlin on 11/23/16.
 */
public enum InstrumentTypeEnum {
    CIVIL_JUDGMENT("民事判决书", "初");

    private String instrumentType;
    private String level;

    InstrumentTypeEnum(String instrumentType, String level) {
        this.instrumentType = instrumentType;
        this.level = level;
    }

    public String getInstrumentType() {
        return this.instrumentType;
    }

    public InstrumentTypeEnum getCivilJudgment() {
        return CIVIL_JUDGMENT;
    };
}
