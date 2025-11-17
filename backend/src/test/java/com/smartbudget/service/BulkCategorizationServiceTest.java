package com.smartbudget.service;

import com.smartbudget.dto.BulkCategorizationJobStatus;
import com.smartbudget.dto.BulkCategorizationRequest;
import com.smartbudget.dto.CategorySuggestion;
import com.smartbudget.entity.Category;
import com.smartbudget.entity.Transaction;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.repository.CategoryRepository;
import com.smartbudget.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulkCategorizationServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategorizationService categorizationService;

    @InjectMocks
    private BulkCategorizationService bulkCategorizationService;

    private UUID userId;
    private Category food;
    private Transaction tx;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        food = new Category();
        food.setId(UUID.randomUUID());
        food.setName("Food");

        tx = new Transaction();
        tx.setId(UUID.randomUUID());
        tx.setTransactionType(TransactionType.EXPENSE);
        tx.setDescription("Lunch at cafe");
        tx.setAmount(BigDecimal.valueOf(12.50));
    }

    @Test
    void startJob_ShouldUpdateTransactionsAboveThreshold() {
        BulkCategorizationRequest request = new BulkCategorizationRequest();
        request.setTransactionType(TransactionType.EXPENSE);
        request.setConfidenceThreshold(0.7);
        request.setDateFrom(LocalDate.now().minusDays(1));
        request.setDateTo(LocalDate.now());

        when(transactionRepository.findForBulkCategorization(any(), any(), any(), any(), any()))
                .thenReturn(List.of(tx));
        when(categorizationService.suggestCategory(any(), any(), any(), any()))
                .thenReturn(new CategorySuggestion(food.getId(), "Food", 0.9));
        when(categoryRepository.findById(food.getId())).thenReturn(Optional.of(food));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(tx);

        BulkCategorizationJobStatus completed = bulkCategorizationService.startJob(userId, request);
        assertThat(completed.getStatus()).isEqualTo(BulkCategorizationJobStatus.Status.COMPLETED);
        assertThat(completed.getTotalUpdated()).isEqualTo(1);
        assertThat(completed.getTotalProcessed()).isEqualTo(1);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        assertThat(txCaptor.getValue().getCategory()).isEqualTo(food);
    }

    @Test
    void startJob_ShouldSkipLowConfidence() {
        BulkCategorizationRequest request = new BulkCategorizationRequest();
        request.setConfidenceThreshold(0.8);

        when(transactionRepository.findForBulkCategorization(any(), any(), any(), any(), any()))
                .thenReturn(List.of(tx));
        when(categorizationService.suggestCategory(any(), any(), any(), any()))
                .thenReturn(new CategorySuggestion(food.getId(), "Food", 0.5));

        BulkCategorizationJobStatus completed = bulkCategorizationService.startJob(userId, request);
        assertThat(completed.getTotalUpdated()).isEqualTo(0);
        assertThat(completed.getTotalSkippedLowConfidence()).isEqualTo(1);
    }
}
