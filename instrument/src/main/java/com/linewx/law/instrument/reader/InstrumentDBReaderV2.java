package com.linewx.law.instrument.reader;

import com.linewx.law.instrument.model.rawdata.Rawdata;
import com.linewx.law.instrument.model.rawdata.RawdataService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by lugan on 11/28/2016.
 */
public class InstrumentDBReaderV2 implements InstrumentReader {
    private Long start = 0L;
    private Long end = 0L;
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
                String rd = rawdata.getNr();
                if (rd == null) {
                    instrumentWithMeta.setContent(new ArrayList<>());
                }else {
                    instrumentWithMeta.setContent(Arrays.asList(rd.split("\r\n")));
                }

                return instrumentWithMeta;
            }
        }
    }

    public InstrumentDBReaderV2(RawdataService rawdataService, Long start, Long end) {
        this.rawdataService = rawdataService;
        this.start = start;
        this.end = end;
    }

    @Override
    public Iterable<List<String>> readBulk(int bulkSize) {
        synchronized (this) {
            List<Rawdata> rawdatas = rawdataService.getData(start, end, bulkSize);
            if (rawdatas != null && !rawdatas.isEmpty()) {
                Rawdata lastData = rawdatas.get(rawdatas.size() - 1);
                start = lastData.getId();
            }
            return rawdatas.stream().map(rawdata -> {
                return Arrays.asList(rawdata.getNr().split("\r\n"));
            }).collect(Collectors.toList());
        }


    }

    @Override
    public Iterable<InstrumentWithMeta> readBulkWithMeta(int bulkSize) {

        synchronized (this) {
            List<Rawdata> rawdatas = rawdataService.getData(start, end, bulkSize);
            if (rawdatas != null && !rawdatas.isEmpty()) {
                Rawdata lastData = rawdatas.get(rawdatas.size() - 1);
                start = lastData.getId();
                return () -> new InstrumentIterator(rawdatas.iterator());
            }else {
                return null;
            }
        }
    }
}
