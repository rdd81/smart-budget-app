package com.smartbudget.repository;

import com.smartbudget.entity.CategorizationFeedback;
import com.smartbudget.repository.projection.FeedbackCategoryCount;
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
}
