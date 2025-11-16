package com.smartbudget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Aggregated summary response for dashboard analytics.
 */
public class SummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal balance;
    private long transactionCount;
    private LocalDate startDate;
    private LocalDate endDate;

    public SummaryResponse() {
    }

    public SummaryResponse(BigDecimal totalIncome,
                           BigDecimal totalExpenses,
                           BigDecimal balance,
                           long transactionCount,
                           LocalDate startDate,
                           LocalDate endDate) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.balance = balance;
        this.transactionCount = transactionCount;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
