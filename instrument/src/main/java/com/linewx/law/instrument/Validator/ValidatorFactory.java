package com.linewx.law.instrument.Validator;

import com.linewx.law.instrument.InstrumentTypeEnum;

/**
 * Created by luganlin on 11/23/16.
 */
public class ValidatorFactory {
    public static Validator getValidator(InstrumentTypeEnum instrumentTypeEnum) {
        switch (instrumentTypeEnum) {
            case CIVIL_JUDGMENT:
                return new CivilJudgementValidator();
            default:
                return null;
        }
    }
}
