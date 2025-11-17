package com.smartbudget.service;

import com.smartbudget.dto.BulkCategorizationJobStatus;
import com.smartbudget.dto.BulkCategorizationRequest;
import com.smartbudget.dto.CategorySuggestion;
import com.smartbudget.entity.Category;
import com.smartbudget.entity.Transaction;
import com.smartbudget.repository.CategoryRepository;
import com.smartbudget.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles bulk auto-categorization jobs.
 */
@Service
public class BulkCategorizationService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final CategorizationService categorizationService;

    private final Map<UUID, BulkCategorizationJobStatus> jobs = new ConcurrentHashMap<>();

    public BulkCategorizationService(TransactionRepository transactionRepository,
                                     CategoryRepository categoryRepository,
                                     CategorizationService categorizationService) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.categorizationService = categorizationService;
    }

    public BulkCategorizationJobStatus startJob(UUID userId, BulkCategorizationRequest request) {
        UUID jobId = UUID.randomUUID();
        BulkCategorizationJobStatus status = new BulkCategorizationJobStatus(jobId, BulkCategorizationJobStatus.Status.PENDING);
        jobs.put(jobId, status);
        runJob(jobId, userId, request);
        return status;
    }

    public BulkCategorizationJobStatus getJob(UUID jobId) {
        return jobs.get(jobId);
    }

    @Transactional
    public void runJob(UUID jobId, UUID userId, BulkCategorizationRequest request) {
        BulkCategorizationJobStatus status = jobs.get(jobId);
        if (status == null) {
            return;
        }
        status.setStatus(BulkCategorizationJobStatus.Status.RUNNING);
        try {
            List<Transaction> transactions = transactionRepository.findForBulkCategorization(
                    userId,
                    request.getTransactionType(),
                    request.getDateFrom(),
                    request.getDateTo(),
                    request.getCurrentCategoryId()
            );

            long updated = 0;
            long skipped = 0;
            double threshold = request.getConfidenceThreshold() != null ? request.getConfidenceThreshold() : 0.7;

            for (Transaction tx : transactions) {
                CategorySuggestion suggestion = categorizationService.suggestCategory(
                        tx.getDescription(),
                        tx.getAmount(),
                        tx.getTransactionType(),
                        userId
                );
                if (suggestion != null && suggestion.getConfidence() >= threshold && suggestion.getCategoryId() != null) {
                    UUID suggestedId = suggestion.getCategoryId();
                    // avoid unnecessary writes if already that category
                    if (tx.getCategory() == null || !suggestedId.equals(tx.getCategory().getId())) {
                        Category target = categoryRepository.findById(suggestedId).orElse(null);
                        if (target != null) {
                            tx.setCategory(target);
                            transactionRepository.save(tx);
                            updated++;
                        } else {
                            skipped++;
                        }
                    }
                } else {
                    skipped++;
                }
            }

            status.setTotalProcessed(transactions.size());
            status.setTotalUpdated(updated);
            status.setTotalSkippedLowConfidence(skipped);
            status.setStatus(BulkCategorizationJobStatus.Status.COMPLETED);
        } catch (Exception ex) {
            status.setStatus(BulkCategorizationJobStatus.Status.FAILED);
            status.setError(ex.getMessage());
        }
    }
}
