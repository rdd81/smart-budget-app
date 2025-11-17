package com.smartbudget.service;

import com.smartbudget.dto.TransactionRequest;
import com.smartbudget.dto.TransactionResponse;
import com.smartbudget.entity.Category;
import com.smartbudget.entity.Transaction;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.entity.User;
import com.smartbudget.exception.ForbiddenOperationException;
import com.smartbudget.exception.ResourceNotFoundException;
import com.smartbudget.repository.CategoryRepository;
import com.smartbudget.repository.TransactionRepository;
import com.smartbudget.repository.UserRepository;
import com.smartbudget.service.FeedbackService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Business logic for transaction CRUD operations with ownership validation.
 */
@Service
public class TransactionService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final String DEFAULT_SORT_PROPERTY = "transactionDate";

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final FeedbackService feedbackService;

    public TransactionService(TransactionRepository transactionRepository,
                              CategoryRepository categoryRepository,
                              UserRepository userRepository,
                              FeedbackService feedbackService) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.feedbackService = feedbackService;
    }

    /**
     * Retrieve paginated transactions for the authenticated user.
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactions(
            UUID userId,
            int page,
            int size,
            String sortBy,
            String sortDirection,
            LocalDate dateFrom,
            LocalDate dateTo,
            List<UUID> categoryIds,
            TransactionType transactionType) {
        int sanitizedPage = Math.max(page, 0);
        int sanitizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Sort sort = resolveSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(sanitizedPage, sanitizedSize, sort);

        List<UUID> categories = categoryIds == null ? Collections.emptyList() : categoryIds;
        boolean categoriesProvided = categories != null && !categories.isEmpty();

        return transactionRepository
                .findByUserIdWithFilters(userId, transactionType, dateFrom, dateTo, categories, categoriesProvided, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Retrieve a single transaction owned by the authenticated user.
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(UUID userId, UUID transactionId) {
        Transaction transaction = fetchOwnedTransaction(transactionId, userId);
        return mapToResponse(transaction);
    }

    /**
     * Create a new transaction for the user.
     */
    @Transactional
    public TransactionResponse createTransaction(UUID userId, TransactionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        applyRequest(transaction, request, category);

        Transaction saved = transactionRepository.save(transaction);
        maybeRecordFeedback(user, request, category, saved);
        return mapToResponse(saved);
    }

    /**
     * Update an existing transaction owned by the user.
     */
    @Transactional
    public TransactionResponse updateTransaction(UUID userId, UUID transactionId, TransactionRequest request) {
        Transaction transaction = fetchOwnedTransaction(transactionId, userId);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        applyRequest(transaction, request, category);
        Transaction saved = transactionRepository.save(transaction);
        maybeRecordFeedback(transaction.getUser(), request, category, saved);
        return mapToResponse(saved);
    }

    /**
     * Delete a transaction owned by the user.
     */
    @Transactional
    public void deleteTransaction(UUID userId, UUID transactionId) {
        Transaction transaction = fetchOwnedTransaction(transactionId, userId);
        transactionRepository.delete(transaction);
    }

    private void applyRequest(Transaction transaction, TransactionRequest request, Category category) {
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setDescription(request.getDescription());
        transaction.setCategory(category);
        transaction.setTransactionType(request.getTransactionType());
    }

    private void maybeRecordFeedback(User user, TransactionRequest request, Category actualCategory, Transaction transaction) {
        if (request.getSuggestedCategoryId() == null) {
            return;
        }
        Category suggested = categoryRepository.findById(request.getSuggestedCategoryId()).orElse(null);
        feedbackService.recordFeedback(user, request.getDescription(), suggested, actualCategory, transaction);
    }

    private Transaction fetchOwnedTransaction(UUID transactionId, UUID userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (transaction.getUser() == null || transaction.getUser().getId() == null) {
            throw new ResourceNotFoundException("Transaction user reference is invalid");
        }

        if (!transaction.getUser().getId().equals(userId)) {
            throw new ForbiddenOperationException("You do not have access to this transaction");
        }

        return transaction;
    }

    private Sort resolveSort(String sortBy, String sortDirection) {
        String normalizedSort = sortBy == null ? DEFAULT_SORT_PROPERTY : sortBy.trim().toLowerCase();
        String property;
        if ("amount".equals(normalizedSort)) {
            property = "amount";
        } else if ("date".equals(normalizedSort) || "transactiondate".equals(normalizedSort)) {
            property = DEFAULT_SORT_PROPERTY;
        } else {
            property = DEFAULT_SORT_PROPERTY;
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, property);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setDescription(transaction.getDescription());
        response.setTransactionType(transaction.getTransactionType());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());

        Category category = transaction.getCategory();
        if (category != null) {
            response.setCategory(new TransactionResponse.CategorySummary(
                    category.getId(),
                    category.getName()
            ));
        }

        return response;
    }
}
