package com.smartbudget.dto;

import com.smartbudget.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for bulk auto-categorization.
 */
public class BulkCategorizationRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private UUID currentCategoryId;

    private TransactionType transactionType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Threshold must be positive")
    private Double confidenceThreshold = 0.7;

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public UUID getCurrentCategoryId() {
        return currentCategoryId;
    }

    public void setCurrentCategoryId(UUID currentCategoryId) {
        this.currentCategoryId = currentCategoryId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Double getConfidenceThreshold() {
        return confidenceThreshold;
    }

    public void setConfidenceThreshold(Double confidenceThreshold) {
        this.confidenceThreshold = confidenceThreshold;
    }
}

