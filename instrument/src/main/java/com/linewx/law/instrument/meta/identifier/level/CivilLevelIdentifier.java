package com.linewx.law.instrument.meta.identifier.level;

import com.linewx.law.instrument.meta.MetaParseContext;
import com.linewx.law.instrument.meta.model.InstrumentLevelEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lugan on 12/16/2016.
 */
public class CivilLevelIdentifier implements LevelIdentifier{
    private Pattern anotherPattern = Pattern.compile(".*(监|申|抗|再|提).*");

    @Override
    public InstrumentLevelEnum identify(MetaParseContext context) {
        String number = context.getNumber();

        if (number.contains("民初")) {
            return InstrumentLevelEnum.FIRST_LEVEL;
        }

        if (number.contains("民终")) {
            return InstrumentLevelEnum.SECOND_LEVEL;
        }

        Matcher matcher = anotherPattern.matcher(number);
        if (matcher.matches()) {
            return InstrumentLevelEnum.ANOTHER_LEVEL;
        }

        return InstrumentLevelEnum.OTHER_LEVEL;
    }
}
