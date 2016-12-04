package com.linewx.law.instrument.reader;

import java.util.List;

/**
 * Created by luganlin on 12/4/16.
 */
public class InstrumentWithMeta {

    Long sourceId; //sourceName id only used in db
    String sourceName; //file name or db url
    String sourceType; // file or db
    List<String> content;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}
