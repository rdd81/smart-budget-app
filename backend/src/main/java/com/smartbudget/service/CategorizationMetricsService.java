package com.smartbudget.service;

import com.smartbudget.dto.CategorizationMetricsResponse;
import com.smartbudget.repository.CategorizationFeedbackRepository;
import com.smartbudget.repository.projection.CategoryMetricsProjection;
import com.smartbudget.repository.projection.MetricsTotalsProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategorizationMetricsService {

    private final CategorizationFeedbackRepository feedbackRepository;

    public CategorizationMetricsService(CategorizationFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional(readOnly = true)
    public CategorizationMetricsResponse getMetrics(LocalDate startDate, LocalDate endDate) {
        Instant start = startDate != null ? startDate.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
        Instant end = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;

        MetricsTotalsProjection totals = feedbackRepository.summarizeTotals(start, end);
        long total = totals != null && totals.getTotal() != null ? totals.getTotal() : 0L;
        long accepted = totals != null && totals.getAccepted() != null ? totals.getAccepted() : 0L;
        long rejected = totals != null && totals.getRejected() != null ? totals.getRejected() : 0L;
        double accuracy = total > 0 ? (double) accepted / total : 0.0;

        List<CategorizationMetricsResponse.CategoryBreakdown> breakdown = feedbackRepository
                .summarizeByCategory(start, end)
                .stream()
                .map(view -> {
                    long catTotal = view.getTotal() == null ? 0 : view.getTotal();
                    long catAccepted = view.getAccepted() == null ? 0 : view.getAccepted();
                    long catRejected = view.getRejected() == null ? 0 : view.getRejected();
                    double catAccuracy = catTotal > 0 ? (double) catAccepted / catTotal : 0.0;
                    return new CategorizationMetricsResponse.CategoryBreakdown(
                            view.getCategoryId(),
                            view.getCategoryName(),
                            catTotal,
                            catAccepted,
                            catRejected,
                            catAccuracy
                    );
                })
                .collect(Collectors.toList());

        return new CategorizationMetricsResponse(total, accepted, rejected, accuracy, breakdown);
    }
}

