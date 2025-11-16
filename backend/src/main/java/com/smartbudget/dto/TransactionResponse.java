package com.smartbudget.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartbudget.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response payload for transaction endpoints.
 */
@Schema(description = "Represents a persisted transaction.")
public class TransactionResponse {

    @Schema(description = "Transaction identifier")
    private UUID id;

    @Schema(description = "Transaction amount")
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Date of the transaction")
    private LocalDate transactionDate;

    @Schema(description = "Optional description of the transaction")
    private String description;

    @Schema(description = "Category summary")
    private CategorySummary category;

    @Schema(description = "Type of the transaction")
    private TransactionType transactionType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Last updated timestamp")
    private LocalDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public CategorySummary getCategory() {
        return category;
    }

    public void setCategory(CategorySummary category) {
        this.category = category;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Nested category summary included in the transaction response.
     */
    public static class CategorySummary {
        @Schema(description = "Category identifier")
        private UUID id;

        @Schema(description = "Category name")
        private String name;

        public CategorySummary() {
        }

        public CategorySummary(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
