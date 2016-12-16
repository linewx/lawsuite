package com.linewx.law.instrument.meta.model;

/**
 * Created by lugan on 12/16/2016.
 */
public enum InstrumentTypeEnum {
    ADJUDGEMENT("判决书"),
    ARBITRATION("裁定书"),
    INFORM("通知书"),
    DECISION("裁定书"),
    CONCILIATION("调解书"),
    ORDER("令"),
    OTHER("其他");

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
