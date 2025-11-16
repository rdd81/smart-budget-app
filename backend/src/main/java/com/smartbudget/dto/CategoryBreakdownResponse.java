package com.smartbudget.dto;

import com.smartbudget.entity.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents aggregated spending per category.
 */
public class CategoryBreakdownResponse {

    private UUID categoryId;
    private String categoryName;
    private TransactionType transactionType;
    private BigDecimal totalAmount;
    private long transactionCount;
    private double percentage;

    public CategoryBreakdownResponse() {
    }

    public CategoryBreakdownResponse(UUID categoryId,
                                     String categoryName,
                                     TransactionType transactionType,
                                     BigDecimal totalAmount,
                                     long transactionCount,
                                     double percentage) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.transactionType = transactionType;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
        this.percentage = percentage;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
