package com.linewx.law.instrument.meta.identifier.domain;

import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.meta.MetaParseContext;
import com.linewx.law.instrument.meta.model.InstrumentDomainEnum;
import com.linewx.law.parser.ParseContext;

import java.util.List;

/**
 * Created by lugan on 12/16/2016.
 */
public class DomainIdentifier {
    public static InstrumentDomainEnum getDomain(MetaParseContext context) {
        if (context.getCompensation()) {
            return InstrumentDomainEnum.COMPENSATION_DOMAIN;
        }

        String number = context.getNumber();

        for (int i=0; i<number.length(); i++) {
            switch (number.charAt(i)) {
                case '民':
                    return InstrumentDomainEnum.CIVIL_DOMAIN;
                case '刑':
                    return InstrumentDomainEnum.CRIMINAL_DOMAIN;
                case '执':
                    return InstrumentDomainEnum.EXECUTION_DOMAIN;
                case '行':
                    return InstrumentDomainEnum.ADMINISTRATION_DOMAIN;
                default:
                    throw  new InstrumentParserException("no domain found", InstrumentErrorCode.METADATA);
            }
        }
        throw  new InstrumentParserException("no domain found", InstrumentErrorCode.METADATA);
    }
}
