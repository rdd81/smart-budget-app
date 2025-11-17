package com.smartbudget.service;

import com.smartbudget.entity.CategorizationFeedback;
import com.smartbudget.entity.Category;
import com.smartbudget.entity.Transaction;
import com.smartbudget.entity.User;
import com.smartbudget.repository.CategorizationFeedbackRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Records user feedback for category suggestions.
 */
@Service
public class FeedbackService {

    private final CategorizationFeedbackRepository feedbackRepository;

    public FeedbackService(CategorizationFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Persist feedback asynchronously and in its own transaction to avoid slowing down the main flow.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFeedback(User user,
                               String description,
                               Category suggestedCategory,
                               Category actualCategory,
                               Transaction transaction) {
        if (user == null || transaction == null || actualCategory == null) {
            return;
        }
        CategorizationFeedback feedback = new CategorizationFeedback();
        feedback.setUser(user);
        feedback.setDescription(description);
        feedback.setSuggestedCategory(suggestedCategory);
        feedback.setActualCategory(actualCategory);
        feedback.setTransaction(transaction);
        feedbackRepository.save(feedback);
    }
}
