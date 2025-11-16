package com.smartbudget.repository.projection;

import java.math.BigDecimal;

/**
 * Projection for aggregated transaction summary.
 */
public interface TransactionSummaryView {
    BigDecimal getIncome();
    BigDecimal getExpenses();
    long getTransactionCount();
}
