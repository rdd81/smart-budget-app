package com.smartbudget.service;

import com.smartbudget.dto.CategorizationMetricsResponse;
import com.smartbudget.repository.CategorizationFeedbackRepository;
import com.smartbudget.repository.projection.CategoryMetricsProjection;
import com.smartbudget.repository.projection.MetricsTotalsProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorizationMetricsServiceTest {

    @Mock
    private CategorizationFeedbackRepository feedbackRepository;

    @InjectMocks
    private CategorizationMetricsService metricsService;

    @Test
    void getMetrics_ShouldComputeAccuracyAndBreakdown() {
        when(feedbackRepository.summarizeTotals(any(), any())).thenReturn(new MetricsTotalsProjection() {
            @Override
            public Long getTotal() {
                return 10L;
            }

            @Override
            public Long getAccepted() {
                return 7L;
            }

            @Override
            public Long getRejected() {
                return 3L;
            }
        });

        UUID categoryId = UUID.randomUUID();
        when(feedbackRepository.summarizeByCategory(any(), any())).thenReturn(List.of(new CategoryMetricsProjection() {
            @Override
            public UUID getCategoryId() {
                return categoryId;
            }

            @Override
            public String getCategoryName() {
                return "Food";
            }

            @Override
            public Long getTotal() {
                return 5L;
            }

            @Override
            public Long getAccepted() {
                return 4L;
            }

            @Override
            public Long getRejected() {
                return 1L;
            }
        }));

        CategorizationMetricsResponse response = metricsService.getMetrics(LocalDate.now().minusDays(7), LocalDate.now());

        assertThat(response.getTotalSuggestions()).isEqualTo(10);
        assertThat(response.getAcceptedSuggestions()).isEqualTo(7);
        assertThat(response.getRejectedSuggestions()).isEqualTo(3);
        assertThat(response.getAccuracy()).isCloseTo(0.7, within(0.0001));
        assertThat(response.getBreakdown()).hasSize(1);
        assertThat(response.getBreakdown().get(0).getCategoryName()).isEqualTo("Food");
        assertThat(response.getBreakdown().get(0).getAccuracy()).isCloseTo(0.8, within(0.0001));
    }
}

