package com.linewx.law.parser.Processor;

/**
 * Created by lugan on 11/21/2016.
 */
public class AmountLevel {
    private Double percentage;
    private Long amountLevel;
    private Long costLevel;

    public AmountLevel( Long amountLevel, Double percentage) {
        this.percentage = percentage;
        this.amountLevel = amountLevel;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Long getAmountLevel() {
        return amountLevel;
    }

    public void setAmountLevel(Long amountLevel) {
        this.amountLevel = amountLevel;
    }

    public Long getCostLevel() {
        return costLevel;
    }

    public void setCostLevel(Long costLevel) {
        this.costLevel = costLevel;
    }
}
