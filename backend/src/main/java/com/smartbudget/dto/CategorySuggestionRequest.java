package com.smartbudget.dto;

import com.smartbudget.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request payload for category suggestion API.
 */
public class CategorySuggestionRequest {

    @NotBlank(message = "Description is required")
    @Schema(description = "Raw transaction description", example = "Starbucks coffee")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Amount must be positive")
    @Digits(integer = 12, fraction = 2, message = "Amount must be a valid currency value")
    @Schema(description = "Transaction amount, optional but improves heuristics", example = "8.75")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    @Schema(description = "Transaction type to scope rules", example = "EXPENSE", requiredMode = Schema.RequiredMode.REQUIRED)
    private TransactionType transactionType;

    public CategorySuggestionRequest() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}

