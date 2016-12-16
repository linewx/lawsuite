package com.linewx.law.instrument.meta.model;

/**
 * Created by lugan on 12/16/2016.
 */
public enum InstrumentLevelEnum {
    FIRST_LEVEL("一审"),
    SECOND_LEVEL("二审"),
    ANOTHER_LEVEL("再审"),
    OTHER_LEVEL("其他"),
    EMPTY_LEVEL("空");

    private final String level;

    InstrumentLevelEnum(String level) {
        this.level = level;
    }

    public String getLevel() {
        return this.level;
    }
}
