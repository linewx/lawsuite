package com.linewx.law.instrument.audit;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luganlin on 11/27/16.
 */
@Component
public class AuditService {
    private Long processed = 0L;
    private Long unsupported = 0L;
    private Long error = 0L;

    public Long getProcessed() {
        return processed;
    }

    public void setProcessed(Long processed) {
        this.processed = processed;
    }

    public Long getUnsupported() {
        return unsupported;
    }

    public void setUnsupported(Long unsupported) {
        this.unsupported = unsupported;
    }

    public Long getError() {
        return error;
    }

    public void setError(Long error) {
        this.error = error;
    }

    synchronized public void increase() {
        processed = processed + 1;
    }

    synchronized public void increaseError() {
        processed = processed + 1;
        error = error + 1;
    }

    synchronized  public void increaseUnsupport() {
        processed = processed + 1;
        unsupported = unsupported + 1;
    }

    synchronized public Map<String, Long> getResult() {
        Map<String, Long> results = new HashMap<>();
        results.put("processed", processed);
        results.put("unsupported", unsupported);
        results.put("error", error);
        return results;
    }


}
