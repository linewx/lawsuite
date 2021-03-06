package com.linewx.law.parser;

import com.linewx.law.parser.Processor.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lugan on 11/18/2016.
 */
public class ProcessorHandler {
    private static Map<String, Processor> processors = new HashMap<>();

    static {
        processors.put("eraseSpace", new EraseSpaceProcessor());
        processors.put("amount", new AmountProcessor());
        processors.put("date", new DateProcessor());
        processors.put("double", new DoubleProcessor());
    }

    public static String execute(String processor, String source) {
        if (processor == null || processor.isEmpty()) {
            return source;
        }

        Processor oneProcessor = processors.get(processor);
        if (oneProcessor == null) {
            return source;
        }else {
            return oneProcessor.transform(source);
        }

    }
}
