package com.smartbudget.service;

import com.smartbudget.dto.CategoryBreakdownResponse;
import com.smartbudget.dto.SummaryResponse;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.repository.TransactionRepository;
import com.smartbudget.repository.projection.CategoryBreakdownView;
import com.smartbudget.repository.projection.TransactionSummaryView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void getSummary_ShouldReturnAggregatedValues() {
        TransactionSummaryView projection = new TransactionSummaryView() {
            @Override
            public BigDecimal getIncome() {
                return new BigDecimal("1000");
            }

            @Override
            public BigDecimal getExpenses() {
                return new BigDecimal("400");
            }

            @Override
            public long getTransactionCount() {
                return 5;
            }
        };

        when(transactionRepository.summarizeTransactions(any(), any(), any())).thenReturn(projection);

        SummaryResponse response = analyticsService.getSummary(userId, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

        assertThat(response.getTotalIncome()).isEqualByComparingTo("1000");
        assertThat(response.getTotalExpenses()).isEqualByComparingTo("400");
        assertThat(response.getBalance()).isEqualByComparingTo("600");
        assertThat(response.getTransactionCount()).isEqualTo(5);
    }

    @Test
    void getSummary_ShouldHandleNullProjection() {
        when(transactionRepository.summarizeTransactions(any(), any(), any())).thenReturn(null);

        SummaryResponse response = analyticsService.getSummary(userId, null, null);

        assertThat(response.getTotalIncome()).isZero();
        assertThat(response.getTotalExpenses()).isZero();
        assertThat(response.getBalance()).isZero();
        assertThat(response.getTransactionCount()).isZero();
    }

    @Test
    void getCategoryBreakdown_ShouldCalculatePercentages() {
        CategoryBreakdownView incomeRow = new CategoryBreakdownView() {
            @Override
            public UUID getCategoryId() {
                return UUID.randomUUID();
            }

            @Override
            public String getCategoryName() {
                return "Salary";
            }

            @Override
            public TransactionType getTransactionType() {
                return TransactionType.INCOME;
            }

            @Override
            public BigDecimal getTotalAmount() {
                return new BigDecimal("2000");
            }

            @Override
            public long getTransactionCount() {
                return 2;
            }
        };

        CategoryBreakdownView expenseRow = new CategoryBreakdownView() {
            @Override
            public UUID getCategoryId() {
                return UUID.randomUUID();
            }

            @Override
            public String getCategoryName() {
                return "Rent";
            }

            @Override
            public TransactionType getTransactionType() {
                return TransactionType.EXPENSE;
            }

            @Override
            public BigDecimal getTotalAmount() {
                return new BigDecimal("800");
            }

            @Override
            public long getTransactionCount() {
                return 1;
            }
        };

        when(transactionRepository.getCategoryBreakdown(any(), any(), any()))
                .thenReturn(List.of(incomeRow, expenseRow));

        List<CategoryBreakdownResponse> responses = analyticsService.getCategoryBreakdown(
                userId,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31));

        assertThat(responses).hasSize(2);
        CategoryBreakdownResponse incomeResponse = responses.stream()
                .filter(r -> r.getTransactionType() == TransactionType.INCOME)
                .findFirst()
                .orElseThrow();
        assertThat(incomeResponse.getPercentage()).isEqualTo(100.0);
    }
}
