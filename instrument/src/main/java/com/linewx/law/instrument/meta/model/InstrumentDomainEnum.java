package com.linewx.law.instrument.meta.model;

/**
 * Created by lugan on 12/16/2016.
 */
public enum InstrumentDomainEnum {
    CIVIL_DOMAIN("民事"),
    CRIMINAL_DOMAIN("刑事"),
    ADMINISTRATION_DOMAIN("行政"),
    EXECUTION_DOMAIN("执行"),
    COMPENSATION_DOMAIN("赔偿"),
    EMPTY_DOMAIN("空");

    private final String domain;

    InstrumentDomainEnum(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return this.domain;
    }


}
