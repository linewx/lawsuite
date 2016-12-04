package com.linewx.law.instrument.reader;

import com.linewx.law.instrument.model.rawdata.Rawdata;
import com.linewx.law.instrument.model.rawdata.RawdataService;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by lugan on 11/28/2016.
 */
public class InstrumentDBReader implements InstrumentReader {
    private int cur = 0;
    private RawdataService rawdataService;

    public static class InstrumentIterator implements Iterator<InstrumentWithMeta> {
        private Iterator<Rawdata> iterator;

        public InstrumentIterator(Iterator<Rawdata> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public InstrumentWithMeta next() {
            Rawdata rawdata = iterator.next();
            if (rawdata == null) {
                return null;
            }else {
                InstrumentWithMeta instrumentWithMeta = new InstrumentWithMeta();
                instrumentWithMeta.setSourceType("db");
                instrumentWithMeta.setSourceId(rawdata.getId());
                instrumentWithMeta.setContent(Arrays.asList(rawdata.getNr().split("\r\n")));
                return instrumentWithMeta;
            }
        }
    }

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
        } else {
            return null;
        }
    }

    @Override
    public Iterable<InstrumentWithMeta> readBulkWithMeta(int bulkSize) {
        int curPosition;

        synchronized (this) {
            curPosition = cur;
            cur = cur + 1;
        }

        Iterable<Rawdata> rawdatas = rawdataService.getData(curPosition, bulkSize);
        return () -> new InstrumentIterator(rawdatas.iterator());

        /*if (rawdatas != null && rawdatas.iterator().hasNext()) {
            Stream<Rawdata> rawdataStream = StreamSupport.stream(rawdatas.spliterator(), false);
            Stream<InstrumentWithMeta> sourceStream = rawdataStream.map(rawdata -> {
                        InstrumentWithMeta instrumentWithMeta = new InstrumentWithMeta();
                        instrumentWithMeta.setSourceType("db");
                        instrumentWithMeta.setSourceId(rawdata.getId());
                        instrumentWithMeta.setContent(Arrays.asList(rawdata.getNr().split("\r\n")));
                        //Arrays.asList(rawdata.getNr().split("\r\n"));
                        return instrumentWithMeta;
                    }
            );
            return sourceStream::iterator;
        } else {
            return null;
        }*/
    }
}
