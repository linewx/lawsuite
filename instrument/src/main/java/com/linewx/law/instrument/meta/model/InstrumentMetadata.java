package com.linewx.law.instrument.meta.model;

/**
 * Created by lugan on 12/16/2016.
 */
public class InstrumentMetadata {
    public InstrumentDomainEnum instrumentDomainEnum;

    public InstrumentLevelEnum instrumentLevelEnum;

    public InstrumentTypeEnum instrumentTypeEnum;

    public InstrumentMetadata() {
        instrumentDomainEnum = InstrumentDomainEnum.EMPTY_DOMAIN;
        instrumentLevelEnum = InstrumentLevelEnum.EMPTY_LEVEL;
        instrumentTypeEnum = InstrumentTypeEnum.EMPTY;
    }

    public InstrumentDomainEnum getInstrumentDomainEnum() {
        return instrumentDomainEnum;
    }

    public void setInstrumentDomainEnum(InstrumentDomainEnum instrumentDomainEnum) {
        this.instrumentDomainEnum = instrumentDomainEnum;
    }

    public InstrumentLevelEnum getInstrumentLevelEnum() {
        return instrumentLevelEnum;
    }

    public void setInstrumentLevelEnum(InstrumentLevelEnum instrumentLevelEnum) {
        this.instrumentLevelEnum = instrumentLevelEnum;
    }

    public InstrumentTypeEnum getInstrumentTypeEnum() {
        return instrumentTypeEnum;
    }

    public void setInstrumentTypeEnum(InstrumentTypeEnum instrumentTypeEnum) {
        this.instrumentTypeEnum = instrumentTypeEnum;
    }
}
