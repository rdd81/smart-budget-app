package com.smartbudget.repository;

import com.smartbudget.entity.Transaction;
import com.smartbudget.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Get total income for a user within a date range.
     *
     * @param userId the user ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the total income amount
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId " +
           "AND t.transactionType = 'INCOME' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalIncomeByUserAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Get total expenses for a user within a date range.
     *
     * @param userId the user ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the total expense amount
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId " +
           "AND t.transactionType = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalExpensesByUserAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
