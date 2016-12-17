package com.linewx.law.instrument.meta.model;

/**
 * Created by lugan on 12/16/2016.
 */
public enum InstrumentTypeEnum {
    ADJUDGEMENT("判决"),
    ARBITRATION("裁定"),
    INFORM("通知"),
    DECISION("决定"),
    CONCILIATION("调解"),
    ORDER("令"),
    OTHER("其他"),
    EMPTY("空");

    private final String type;

    InstrumentTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static InstrumentTypeEnum getTypeByName(String name) {
        if (name == null) {
            return OTHER;
        }

        for (InstrumentTypeEnum oneType: InstrumentTypeEnum.values()) {
            if (oneType.getType().equals(name)) {
                return oneType;
            }
        }
        return OTHER;
    }

}
