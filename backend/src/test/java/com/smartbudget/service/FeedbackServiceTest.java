package com.smartbudget.service;

import com.smartbudget.entity.CategorizationFeedback;
import com.smartbudget.entity.Category;
import com.smartbudget.entity.Transaction;
import com.smartbudget.entity.User;
import com.smartbudget.repository.CategorizationFeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private CategorizationFeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private User user;
    private Category suggested;
    private Category actual;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        user = new User();
        suggested = new Category();
        actual = new Category();
        transaction = new Transaction();

        suggested.setId(java.util.UUID.randomUUID());
        actual.setId(java.util.UUID.randomUUID());
    }

    @Test
    void recordFeedback_ShouldPersistWhenDifferent() {
        feedbackService.recordFeedback(user, "desc", suggested, actual, transaction);

        ArgumentCaptor<CategorizationFeedback> captor = ArgumentCaptor.forClass(CategorizationFeedback.class);
        verify(feedbackRepository).save(captor.capture());
        assertThat(captor.getValue().getActualCategory()).isEqualTo(actual);
        assertThat(captor.getValue().getSuggestedCategory()).isEqualTo(suggested);
        assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    void recordFeedback_ShouldSkipWhenSameCategory() {
        actual.setId(suggested.getId());
        feedbackService.recordFeedback(user, "desc", suggested, actual, transaction);
        verify(feedbackRepository, never()).save(org.mockito.Mockito.any());
    }

    @Test
    void recordFeedback_ShouldSkipWhenMissingUser() {
        feedbackService.recordFeedback(null, "desc", suggested, actual, transaction);
        verify(feedbackRepository, never()).save(org.mockito.Mockito.any());
    }
}

