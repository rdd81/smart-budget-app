package com.smartbudget.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents income/expense totals grouped by a time period.
 */
public class TrendDataPoint {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate period;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal net;
    private long transactionCount;

    public TrendDataPoint() {
    }

    public TrendDataPoint(LocalDate period, BigDecimal totalIncome, BigDecimal totalExpenses, long transactionCount) {
        this.period = period;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.net = totalIncome.subtract(totalExpenses);
        this.transactionCount = transactionCount;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNet() {
        return net;
    }

    public void setNet(BigDecimal net) {
        this.net = net;
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(long transactionCount) {
        this.transactionCount = transactionCount;
    }
}
