package com.smartbudget.service;

import com.smartbudget.dto.CategoryBreakdownResponse;
import com.smartbudget.dto.SummaryResponse;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.repository.TransactionRepository;
import com.smartbudget.repository.projection.CategoryBreakdownView;
import com.smartbudget.repository.projection.TransactionSummaryView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Provides analytics aggregations for dashboard endpoints.
 */
@Service
public class AnalyticsService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final TransactionRepository transactionRepository;

    public AnalyticsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public SummaryResponse getSummary(UUID userId, LocalDate startDate, LocalDate endDate) {
        DateRange range = resolveDateRange(startDate, endDate);
        TransactionSummaryView summary = transactionRepository.summarizeTransactions(userId, range.start(), range.end());

        BigDecimal totalIncome = summary != null && summary.getIncome() != null ? summary.getIncome() : ZERO;
        BigDecimal totalExpenses = summary != null && summary.getExpenses() != null ? summary.getExpenses() : ZERO;
        long transactionCount = summary != null ? summary.getTransactionCount() : 0;
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        return new SummaryResponse(
                totalIncome,
                totalExpenses,
                balance,
                transactionCount,
                range.start(),
                range.end()
        );
    }

    @Transactional(readOnly = true)
    public List<CategoryBreakdownResponse> getCategoryBreakdown(UUID userId, LocalDate startDate, LocalDate endDate) {
        DateRange range = resolveDateRange(startDate, endDate);
        List<CategoryBreakdownView> rows = transactionRepository.getCategoryBreakdown(userId, range.start(), range.end());

        Map<TransactionType, BigDecimal> totalsByType = new EnumMap<>(TransactionType.class);
        for (CategoryBreakdownView row : rows) {
            totalsByType.merge(row.getTransactionType(), safeAmount(row.getTotalAmount()), BigDecimal::add);
        }

        List<CategoryBreakdownResponse> responses = new ArrayList<>();
        for (CategoryBreakdownView row : rows) {
            BigDecimal typeTotal = totalsByType.getOrDefault(row.getTransactionType(), ZERO);
            double percentage = typeTotal.compareTo(ZERO) == 0
                    ? 0
                    : safeAmount(row.getTotalAmount())
                        .divide(typeTotal, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();

            responses.add(new CategoryBreakdownResponse(
                    row.getCategoryId(),
                    row.getCategoryName(),
                    row.getTransactionType(),
                    safeAmount(row.getTotalAmount()),
                    row.getTransactionCount(),
                    percentage
            ));
        }

        return responses;
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? ZERO : amount;
    }

    private DateRange resolveDateRange(LocalDate start, LocalDate end) {
        LocalDate resolvedStart = start != null ? start : YearMonth.now().atDay(1);
        LocalDate resolvedEnd;
        if (end != null) {
            resolvedEnd = end;
        } else if (start != null) {
            resolvedEnd = start.withDayOfMonth(start.lengthOfMonth());
        } else {
            YearMonth currentMonth = YearMonth.now();
            resolvedEnd = currentMonth.atEndOfMonth();
        }

        if (resolvedStart.isAfter(resolvedEnd)) {
            throw new IllegalArgumentException("The 'startDate' cannot be after 'endDate'.");
        }

        return new DateRange(resolvedStart, resolvedEnd);
    }

    private record DateRange(LocalDate start, LocalDate end) {
    }
}
