package com.smartbudget.dto;

import com.smartbudget.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

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

    @Schema(description = "User identifier to personalize suggestions (optional)", example = "1d0f0b6e-9c6b-4ee5-9d7e-6a8e2fd6d9be")
    private UUID userId;

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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
