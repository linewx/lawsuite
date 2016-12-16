package com.linewx.law.instrument.meta.identifier.level;

import com.linewx.law.instrument.meta.MetaParseContext;
import com.linewx.law.instrument.meta.model.InstrumentLevelEnum;
import com.linewx.law.instrument.model.Instrument;

/**
 * Created by lugan on 12/16/2016.
 */
public interface LevelIdentifier {
    InstrumentLevelEnum identify(MetaParseContext context);
}
