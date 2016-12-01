package com.linewx.law.instrument.reader;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lugan on 11/28/2016.
 */
public class InstrumentFilesReader implements InstrumentReader{
    private File [] files = null;
    private int cur = 0;

    public InstrumentFilesReader(String folder) {
        File sourceFolder = new File(folder);
        files = sourceFolder.listFiles();
    }

    @Override
   public Iterable<List<String>> readBulk(int bulkSize) {

        int curPosition;
        int nextPosition;

        synchronized (this) {
            curPosition = cur;
            nextPosition = (cur + bulkSize) <= files.length ? (cur + bulkSize):files.length;
            cur = nextPosition;
        }

        if (curPosition >= files.length) {
            return null;
        }
        List<List<String>> results = new ArrayList<>();

        for (int i = curPosition; i<nextPosition; i++) {
            try {
                List<String> oneFileContent = Files.readLines(files[i], Charsets.UTF_8);
                results.add(oneFileContent);
            }catch(Exception e) {

            }
        }
        return results;
    }
}
