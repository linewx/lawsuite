package com.linewx.law.instrument.reader;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.linewx.law.instrument.model.rawdata.Rawdata;
import com.linewx.law.instrument.model.rawdata.RawdataService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by lugan on 11/28/2016.
 */
public class InstrumentDBReader implements InstrumentReader{
    private int cur = 0;
    private RawdataService rawdataService;

    public InstrumentDBReader(RawdataService rawdataService) {
        this.rawdataService = rawdataService;
    }

    @Override
   public Iterable<List<String>> readBulk(int bulkSize) {

        int curPosition;

        synchronized (this) {
            curPosition = cur;
            cur = cur + 1;
        }

        Iterable<Rawdata> rawdatas = rawdataService.getData(curPosition, bulkSize);
        if (rawdatas != null && rawdatas.iterator().hasNext()) {
            Stream<Rawdata> rawdataStream = StreamSupport.stream(rawdatas.spliterator(), false);
            Stream<List<String>> sourceStream = rawdataStream.map(rawdata -> Arrays.asList(rawdata.getNr().split("\r\n")));
            return sourceStream::iterator;
        }else {
            return null;
        }


    }
}
