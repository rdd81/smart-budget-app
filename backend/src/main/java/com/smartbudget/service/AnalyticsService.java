package com.smartbudget.service;

import com.smartbudget.dto.CategoryBreakdownResponse;
import com.smartbudget.dto.SummaryResponse;
import com.smartbudget.dto.TrendDataPoint;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.repository.TransactionRepository;
import com.smartbudget.repository.projection.CategoryBreakdownView;
import com.smartbudget.repository.projection.TransactionSummaryView;
import com.smartbudget.repository.projection.TrendAggregationView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
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

    @Transactional(readOnly = true)
    public List<TrendDataPoint> getTrends(UUID userId, LocalDate startDate, LocalDate endDate, String groupBy) {
        DateRange range = resolveDateRange(startDate, endDate);
        Grouping grouping = Grouping.from(groupBy);

        if (grouping == Grouping.MONTH) {
            range = expandToMonthBoundaries(range);
        } else if (grouping == Grouping.WEEK) {
            range = expandToWeekBoundaries(range);
        }

        List<TrendAggregationView> daily = transactionRepository.aggregateDaily(userId, range.start(), range.end());
        Map<LocalDate, TrendAggregationView> dailyMap = new java.util.HashMap<>();
        for (TrendAggregationView view : daily) {
            dailyMap.put(view.getPeriod(), view);
        }

        List<TrendDataPoint> result = new ArrayList<>();
        LocalDate cursor = range.start();
        while (!cursor.isAfter(range.end())) {
            LocalDate periodStart = grouping.normalizeStart(cursor);
            LocalDate periodEnd = grouping.normalizeEnd(periodStart, range.end());

            BigDecimal income = BigDecimal.ZERO;
            BigDecimal expenses = BigDecimal.ZERO;
            long transactionCount = 0;

            LocalDate iterator = periodStart;
            while (!iterator.isAfter(periodEnd)) {
                TrendAggregationView view = dailyMap.get(iterator);
                if (view != null) {
                    income = income.add(safeAmount(view.getIncome()));
                    expenses = expenses.add(safeAmount(view.getExpenses()));
                    transactionCount += view.getTransactionCount();
                }
                iterator = iterator.plusDays(1);
            }

            result.add(new TrendDataPoint(periodStart, income, expenses, transactionCount));
            cursor = periodEnd.plusDays(1);
        }

        return result;
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

    private DateRange expandToWeekBoundaries(DateRange original) {
        LocalDate start = original.start();
        LocalDate end = original.end();
        LocalDate normalizedStart = start.with(DayOfWeek.MONDAY);
        LocalDate normalizedEnd = end.with(DayOfWeek.SUNDAY);
        return new DateRange(normalizedStart, normalizedEnd);
    }

    private DateRange expandToMonthBoundaries(DateRange original) {
        LocalDate start = original.start();
        LocalDate end = original.end();
        LocalDate normalizedStart = start.withDayOfMonth(1);
        LocalDate normalizedEnd = end.withDayOfMonth(end.lengthOfMonth());
        return new DateRange(normalizedStart, normalizedEnd);
    }

    private enum Grouping {
        DAY {
            @Override
            LocalDate normalizeStart(LocalDate date) {
                return date;
            }

            @Override
            LocalDate normalizeEnd(LocalDate start, LocalDate maxEnd) {
                return start;
            }
        },
        WEEK {
            @Override
            LocalDate normalizeStart(LocalDate date) {
                return date.with(DayOfWeek.MONDAY);
            }

            @Override
            LocalDate normalizeEnd(LocalDate start, LocalDate maxEnd) {
                LocalDate end = start.with(DayOfWeek.SUNDAY);
                return end.isAfter(maxEnd) ? maxEnd : end;
            }
        },
        MONTH {
            @Override
            LocalDate normalizeStart(LocalDate date) {
                return date.withDayOfMonth(1);
            }

            @Override
            LocalDate normalizeEnd(LocalDate start, LocalDate maxEnd) {
                LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
                return end.isAfter(maxEnd) ? maxEnd : end;
            }
        };

        abstract LocalDate normalizeStart(LocalDate date);

        abstract LocalDate normalizeEnd(LocalDate start, LocalDate maxEnd);

        static Grouping from(String value) {
            if (value == null) {
                return MONTH;
            }
            return switch (value.trim().toUpperCase()) {
                case "DAY" -> DAY;
                case "WEEK" -> WEEK;
                case "MONTH" -> MONTH;
                default -> throw new IllegalArgumentException("Unsupported groupBy value. Use DAY, WEEK, or MONTH.");
            };
        }
    }

    private record DateRange(LocalDate start, LocalDate end) {
    }
}
