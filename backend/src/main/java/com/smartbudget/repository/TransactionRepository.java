package com.smartbudget.repository;

import com.smartbudget.entity.Transaction;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.repository.projection.CategoryBreakdownView;
import com.smartbudget.repository.projection.TransactionSummaryView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Transaction entity.
 * Provides CRUD operations and custom query methods for transactions.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /**
     * Find all transactions for a specific user.
     *
     * @param userId the user ID
     * @return a list of transactions for the user
     */
    List<Transaction> findByUserId(UUID userId);

    /**
     * Find all transactions for a user within a date range.
     *
     * @param userId the user ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of transactions within the date range
     */
    List<Transaction> findByUserIdAndTransactionDateBetween(
            UUID userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find all transactions for a user of a specific type.
     *
     * @param userId the user ID
     * @param transactionType the transaction type (INCOME or EXPENSE)
     * @return a list of transactions matching the type
     */
    List<Transaction> findByUserIdAndTransactionType(
            UUID userId, TransactionType transactionType);

    /**
     * Find all transactions for a user in a specific category.
     *
     * @param userId the user ID
     * @param categoryId the category ID
     * @return a list of transactions in the specified category
     */
    List<Transaction> findByUserIdAndCategoryId(UUID userId, UUID categoryId);

    /**
     * Paged retrieval of transactions for a user with optional filters.
     */
    @Query("""
            SELECT t FROM Transaction t
            WHERE t.user.id = :userId
            AND (:transactionType IS NULL OR t.transactionType = :transactionType)
            AND (:dateFrom IS NULL OR t.transactionDate >= :dateFrom)
            AND (:dateTo IS NULL OR t.transactionDate <= :dateTo)
            AND (:categoriesProvided = false OR t.category.id IN :categoryIds)
            """)
    Page<Transaction> findByUserIdWithFilters(
            UUID userId,
            TransactionType transactionType,
            LocalDate dateFrom,
            LocalDate dateTo,
            List<UUID> categoryIds,
            boolean categoriesProvided,
            Pageable pageable);

    /**
     * Aggregated summary for income/expenses/count.
     */
    @Query("""
            SELECT
                COALESCE(SUM(CASE WHEN t.transactionType = com.smartbudget.entity.TransactionType.INCOME THEN t.amount ELSE 0 END), 0) AS income,
                COALESCE(SUM(CASE WHEN t.transactionType = com.smartbudget.entity.TransactionType.EXPENSE THEN t.amount ELSE 0 END), 0) AS expenses,
                COUNT(t) AS transactionCount
            FROM Transaction t
            WHERE t.user.id = :userId
              AND t.transactionDate BETWEEN :startDate AND :endDate
            """)
    TransactionSummaryView summarizeTransactions(UUID userId, LocalDate startDate, LocalDate endDate);

    /**
     * Aggregated totals per category and transaction type.
     */
    @Query("""
            SELECT t.category.id AS categoryId,
                   t.category.name AS categoryName,
                   t.transactionType AS transactionType,
                   SUM(t.amount) AS totalAmount,
                   COUNT(t) AS transactionCount
            FROM Transaction t
            WHERE t.user.id = :userId
              AND t.transactionDate BETWEEN :startDate AND :endDate
            GROUP BY t.category.id, t.category.name, t.transactionType
            ORDER BY totalAmount DESC
            """)
    List<CategoryBreakdownView> getCategoryBreakdown(UUID userId, LocalDate startDate, LocalDate endDate);
}
