package com.linewx.law.instrument.meta.identifier.level;

import com.linewx.law.instrument.meta.MetaParseContext;
import com.linewx.law.instrument.meta.model.InstrumentLevelEnum;

/**
 * Created by lugan on 12/16/2016.
 */
public class CompensationLevelIdentifier implements LevelIdentifier{
    @Override
    public InstrumentLevelEnum identify(MetaParseContext context) {
        return InstrumentLevelEnum.EMPTY_LEVEL;
    }
}
