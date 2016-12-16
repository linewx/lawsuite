package com.linewx.law.instrument.meta.identifier.level;

import com.linewx.law.instrument.meta.MetaParseContext;
import com.linewx.law.instrument.meta.model.InstrumentLevelEnum;

/**
 * Created by lugan on 12/16/2016.
 */
public class AdministrationLevelIdentifier implements LevelIdentifier{
    @Override
    public InstrumentLevelEnum identify(MetaParseContext context) {
        String number = context.getNumber();

        if (number.contains("初")) {
            return InstrumentLevelEnum.FIRST_LEVEL;
        }

        if (number.contains("终")) {
            return InstrumentLevelEnum.SECOND_LEVEL;
        }

        if (number.contains("行提") || number.contains("知行")) {
            return InstrumentLevelEnum.ANOTHER_LEVEL;
        }

        //todo: to be clarified
        return InstrumentLevelEnum.OTHER_LEVEL;
    }
}
