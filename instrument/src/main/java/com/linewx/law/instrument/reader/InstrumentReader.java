package com.linewx.law.instrument.reader;

import java.util.List;

/**
 * Created by lugan on 11/28/2016.
 */
public interface InstrumentReader {
    //List<String> read();


    List<List<String>> readBulk(int bulkSize);
}
