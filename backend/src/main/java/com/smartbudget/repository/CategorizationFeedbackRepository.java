package com.smartbudget.repository;

import com.smartbudget.entity.CategorizationFeedback;
import com.smartbudget.repository.projection.CategoryMetricsProjection;
import com.smartbudget.repository.projection.FeedbackCategoryCount;
import com.smartbudget.repository.projection.MetricsTotalsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * Repository for categorization feedback entries.
 */
public interface CategorizationFeedbackRepository extends JpaRepository<CategorizationFeedback, UUID> {

    @Query("""
            SELECT f.actualCategory AS category, COUNT(f.id) AS correctionCount
            FROM CategorizationFeedback f
            WHERE f.user.id = :userId
              AND LOWER(f.description) LIKE LOWER(CONCAT('%', :token, '%'))
            GROUP BY f.actualCategory
            ORDER BY correctionCount DESC
            """)
    List<FeedbackCategoryCount> findTopCategoriesForUserAndToken(UUID userId, String token);

    @Query("""
            SELECT COUNT(f.id) AS total,
                   SUM(CASE WHEN f.suggestedCategory.id = f.actualCategory.id THEN 1 ELSE 0 END) AS accepted,
                   SUM(CASE WHEN f.suggestedCategory.id <> f.actualCategory.id THEN 1 ELSE 0 END) AS rejected
            FROM CategorizationFeedback f
            WHERE (:start IS NULL OR f.createdAt >= :start)
              AND (:end IS NULL OR f.createdAt <= :end)
            """)
    MetricsTotalsProjection summarizeTotals(java.time.Instant start, java.time.Instant end);

    @Query("""
            SELECT f.actualCategory.id AS categoryId,
                   f.actualCategory.name AS categoryName,
                   COUNT(f.id) AS total,
                   SUM(CASE WHEN f.suggestedCategory.id = f.actualCategory.id THEN 1 ELSE 0 END) AS accepted,
                   SUM(CASE WHEN f.suggestedCategory.id <> f.actualCategory.id THEN 1 ELSE 0 END) AS rejected
            FROM CategorizationFeedback f
            WHERE (:start IS NULL OR f.createdAt >= :start)
              AND (:end IS NULL OR f.createdAt <= :end)
            GROUP BY f.actualCategory.id, f.actualCategory.name
            ORDER BY total DESC
            """)
    List<CategoryMetricsProjection> summarizeByCategory(java.time.Instant start, java.time.Instant end);
}
