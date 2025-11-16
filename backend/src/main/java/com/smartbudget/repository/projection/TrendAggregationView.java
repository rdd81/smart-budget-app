package com.smartbudget.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TrendAggregationView {
    LocalDate getPeriod();
    BigDecimal getIncome();
    BigDecimal getExpenses();
    long getTransactionCount();
}
