package com.smartbudget.repository;

import com.smartbudget.entity.CategorizationFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for categorization feedback entries.
 */
public interface CategorizationFeedbackRepository extends JpaRepository<CategorizationFeedback, UUID> {
}

