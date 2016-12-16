package com.linewx.law.instrument.meta.identifier.type;

import com.linewx.law.instrument.meta.MetaParseContext;
import com.linewx.law.instrument.meta.model.InstrumentTypeEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lugan on 12/16/2016.
 */
public class TypeIdentifier {
    private static Pattern typePattern;
    static {
        typePattern = Pattern.compile("(判决书|裁定书|通知书|决定书|调解书|令)");
    }
    public static InstrumentTypeEnum identify(MetaParseContext context) {
        String typeResult = context.getType();

        Matcher matcher = typePattern.matcher(typeResult);
        if (matcher.find()) {
            String matchedType = matcher.group(1);

            return InstrumentTypeEnum.getTypeByName(matchedType);
        }else {
            return InstrumentTypeEnum.OTHER;
        }
    }
}
