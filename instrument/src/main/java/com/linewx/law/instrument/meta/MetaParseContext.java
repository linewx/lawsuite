package com.linewx.law.instrument.meta;

import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.parser.ParseContext;

import java.util.List;

/**
 * Created by lugan on 12/16/2016.
 */
public class MetaParseContext {
    private String number; //案号

    private String type; //文书标题

    private Boolean isCompensation; //赔偿案

    private Boolean isConciliation; //调解书

    public MetaParseContext(ParseContext context) {
        List<String> numberResults = context.getResults().get("number");

        if (numberResults == null || numberResults.isEmpty()) {
            throw new InstrumentParserException("no number found", InstrumentErrorCode.METADATA);
        }

        this.number = numberResults.get(0);

        List<String> typeResults = context.getResults().get("instrumentType");

        if (typeResults == null || typeResults.isEmpty()) {
            throw  new InstrumentParserException("no instrument type found", InstrumentErrorCode.METADATA);
        }

        this.type = typeResults.get(0);

        List<String> compensationResults = context.getResults().get("compensation");

        if (compensationResults == null || compensationResults.isEmpty()) {
            this.isCompensation = false;
        }else {
            this.isCompensation = true;
        }

        List<String> conciliationResults = context.getResults().get("conciliation");

        if (conciliationResults == null || conciliationResults.isEmpty()) {
            this.isConciliation = false;
        }else {
            this.isConciliation = true;
        }

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getCompensation() {
        return isCompensation;
    }

    public void setCompensation(Boolean compensation) {
        isCompensation = compensation;
    }

    public Boolean getConciliation() {
        return isConciliation;
    }

    public void setConciliation(Boolean conciliation) {
        isConciliation = conciliation;
    }
}
