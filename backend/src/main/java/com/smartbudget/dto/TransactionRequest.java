package com.smartbudget.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartbudget.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating or updating a transaction.
 */
@Schema(description = "Transaction payload used for create and update operations.")
public class TransactionRequest {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    @Schema(description = "Transaction amount", example = "149.99")
    private BigDecimal amount;

    @NotNull
    @PastOrPresent(message = "Transaction date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Date of the transaction", example = "2024-01-15")
    private LocalDate transactionDate;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Schema(description = "Optional description", example = "Groceries at local market")
    private String description;

    @NotNull
    @Schema(description = "Identifier of the category associated with the transaction")
    private UUID categoryId;

    @NotNull
    @Schema(description = "Type of the transaction", example = "EXPENSE")
    private TransactionType transactionType;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
