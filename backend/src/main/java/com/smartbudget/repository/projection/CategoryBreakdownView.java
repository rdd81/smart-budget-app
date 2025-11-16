package com.smartbudget.repository.projection;

import com.smartbudget.entity.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Projection for category-wise totals.
 */
public interface CategoryBreakdownView {
    UUID getCategoryId();
    String getCategoryName();
    TransactionType getTransactionType();
    BigDecimal getTotalAmount();
    long getTransactionCount();
}
