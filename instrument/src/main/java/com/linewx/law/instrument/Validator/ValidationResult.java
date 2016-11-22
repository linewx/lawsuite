package com.linewx.law.instrument.Validator;

/**
 * Created by luganlin on 11/22/16.
 */
public class ValidationResult {
    Boolean result;
    String message;

    public ValidationResult(Boolean result, String message) {
        this.result = this.result;
        this.message = message;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
