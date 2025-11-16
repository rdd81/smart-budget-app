package com.smartbudget.service;

import com.smartbudget.dto.TransactionRequest;
import com.smartbudget.dto.TransactionResponse;
import com.smartbudget.entity.Category;
import com.smartbudget.entity.CategoryType;
import com.smartbudget.entity.Transaction;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.entity.User;
import com.smartbudget.exception.ForbiddenOperationException;
import com.smartbudget.exception.ResourceNotFoundException;
import com.smartbudget.repository.CategoryRepository;
import com.smartbudget.repository.TransactionRepository;
import com.smartbudget.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TransactionService}.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private UUID userId;
    private User user;
    private Category category;
    private Transaction transaction;
    private TransactionRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");

        category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Groceries");
        category.setType(CategoryType.EXPENSE);

        transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal("45.67"));
        transaction.setTransactionDate(LocalDate.now().minusDays(1));
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setDescription("Lunch");
        transaction.setCreatedAt(LocalDateTime.now().minusDays(2));
        transaction.setUpdatedAt(LocalDateTime.now().minusDays(1));

        request = new TransactionRequest();
        request.setAmount(new BigDecimal("99.99"));
        request.setTransactionDate(LocalDate.now());
        request.setDescription("Weekly shopping");
        request.setCategoryId(category.getId());
        request.setTransactionType(TransactionType.EXPENSE);
    }

    @Test
    void getTransactions_ShouldReturnMappedPage() {
        Page<Transaction> page = new PageImpl<>(List.of(transaction));
        when(transactionRepository.findByUserIdWithFilters(eq(userId), isNull(), isNull(), isNull(), anyList(), eq(false), any(Pageable.class)))
                .thenReturn(page);

        Page<TransactionResponse> result = transactionService.getTransactions(userId, 0, 5, "amount", "asc", null, null, null, null);

        assertThat(result.getContent()).hasSize(1);
        TransactionResponse response = result.getContent().get(0);
        assertThat(response.getId()).isEqualTo(transaction.getId());
        assertThat(response.getCategory().getName()).isEqualTo("Groceries");
        verify(transactionRepository).findByUserIdWithFilters(eq(userId), isNull(), isNull(), isNull(), anyList(), eq(false), any(Pageable.class));
    }

    @Test
    void getTransactions_WithFilters_ShouldApplyParameters() {
        Page<Transaction> page = new PageImpl<>(List.of(transaction));
        UUID categoryId = UUID.randomUUID();
        when(transactionRepository.findByUserIdWithFilters(eq(userId), eq(TransactionType.INCOME),
                eq(LocalDate.of(2025, 1, 1)), eq(LocalDate.of(2025, 1, 31)),
                eq(List.of(categoryId)), eq(true), any(Pageable.class))).thenReturn(page);

        Page<TransactionResponse> result = transactionService.getTransactions(
                userId,
                0,
                10,
                "transactionDate",
                "desc",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31),
                List.of(categoryId),
                TransactionType.INCOME);

        assertThat(result.getContent()).hasSize(1);
        verify(transactionRepository).findByUserIdWithFilters(eq(userId), eq(TransactionType.INCOME),
                eq(LocalDate.of(2025, 1, 1)), eq(LocalDate.of(2025, 1, 31)),
                eq(List.of(categoryId)), eq(true), any(Pageable.class));
    }

    @Test
    void getTransaction_WithMissingEntity_ShouldThrowNotFound() {
        UUID transactionId = UUID.randomUUID();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransaction(userId, transactionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found");
    }

    @Test
    void getTransaction_WithDifferentOwner_ShouldThrowForbidden() {
        UUID transactionId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        User otherUser = new User();
        otherUser.setId(otherUserId);
        transaction.setUser(otherUser);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        assertThatThrownBy(() -> transactionService.getTransaction(userId, transactionId))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void createTransaction_ShouldPersistNewEntity() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            saved.setCreatedAt(LocalDateTime.now());
            saved.setUpdatedAt(LocalDateTime.now());
            return saved;
        });

        TransactionResponse response = transactionService.createTransaction(userId, request);

        assertThat(response.getAmount()).isEqualByComparingTo(request.getAmount());
        assertThat(response.getCategory().getId()).isEqualTo(category.getId());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_WithMissingCategory_ShouldThrowNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(userId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found");
    }

    @Test
    void updateTransaction_ShouldApplyChanges() {
        UUID transactionId = transaction.getId();
        Category newCategory = new Category();
        newCategory.setId(UUID.randomUUID());
        newCategory.setName("Utilities");
        newCategory.setType(CategoryType.EXPENSE);
        request.setCategoryId(newCategory.getId());

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.of(newCategory));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionResponse response = transactionService.updateTransaction(userId, transactionId, request);

        assertThat(response.getAmount()).isEqualByComparingTo(request.getAmount());
        assertThat(response.getCategory().getId()).isEqualTo(newCategory.getId());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void deleteTransaction_ShouldRemoveEntity() {
        UUID transactionId = transaction.getId();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(userId, transactionId);

        verify(transactionRepository).delete(transaction);
    }
}
