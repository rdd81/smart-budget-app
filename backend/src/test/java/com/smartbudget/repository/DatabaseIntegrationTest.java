package com.smartbudget.repository;

import com.smartbudget.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for database setup, migrations, and repository operations.
 * Uses Testcontainers to spin up a real PostgreSQL instance for testing.
 */
@SpringBootTest
@Testcontainers
class DatabaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void testPostgreSQLContainerIsRunning() {
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void testFlywayMigrationsExecutedSuccessfully() {
        // Test that categories table was seeded with 11 categories
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(11);
    }

    @Test
    void testCategorySeededDataIntegrity() {
        // Verify income categories
        List<Category> incomeCategories = categoryRepository.findByType(CategoryType.INCOME);
        assertThat(incomeCategories).hasSize(3);
        assertThat(incomeCategories)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("Salary", "Investments", "Other");

        // Verify expense categories
        List<Category> expenseCategories = categoryRepository.findByType(CategoryType.EXPENSE);
        assertThat(expenseCategories).hasSize(8);
        assertThat(expenseCategories)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("Rent", "Transport", "Food", "Entertainment",
                        "Utilities", "Healthcare", "Shopping", "Savings");
    }

    @Test
    void testUserRepositorySaveAndFind() {
        // Create and save a user
        User user = new User("test@example.com", "hashedpassword123");
        User savedUser = userRepository.save(user);

        // Verify save
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashedpassword123");
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();

        // Test findByEmail
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());

        // Test existsByEmail
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    void testTransactionRepositoryWithForeignKeys() {
        // Create user
        User user = new User("transaction@example.com", "password");
        user = userRepository.save(user);

        // Get a category
        Category category = categoryRepository.findByType(CategoryType.EXPENSE).get(0);

        // Create transaction
        Transaction transaction = new Transaction(
                user,
                new BigDecimal("100.50"),
                LocalDate.now(),
                "Test expense",
                category,
                TransactionType.EXPENSE
        );
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Verify save
        assertThat(savedTransaction.getId()).isNotNull();
        assertThat(savedTransaction.getAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
        assertThat(savedTransaction.getDescription()).isEqualTo("Test expense");
        assertThat(savedTransaction.getTransactionType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(savedTransaction.getCreatedAt()).isNotNull();
        assertThat(savedTransaction.getUpdatedAt()).isNotNull();

        // Test findByUserId
        List<Transaction> userTransactions = transactionRepository.findByUserId(user.getId());
        assertThat(userTransactions).hasSize(1);
        assertThat(userTransactions.get(0).getId()).isEqualTo(savedTransaction.getId());

        // Test findByUserIdAndTransactionType
        List<Transaction> expenseTransactions = transactionRepository
                .findByUserIdAndTransactionType(user.getId(), TransactionType.EXPENSE);
        assertThat(expenseTransactions).hasSize(1);

        List<Transaction> incomeTransactions = transactionRepository
                .findByUserIdAndTransactionType(user.getId(), TransactionType.INCOME);
        assertThat(incomeTransactions).isEmpty();
    }

    @Test
    void testTransactionDateRangeQuery() {
        // Create user
        User user = new User("daterange@example.com", "password");
        user = userRepository.save(user);

        // Get categories
        Category incomeCategory = categoryRepository.findByType(CategoryType.INCOME).get(0);
        Category expenseCategory = categoryRepository.findByType(CategoryType.EXPENSE).get(0);

        // Create transactions with different dates
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastWeek = today.minusDays(7);

        transactionRepository.save(new Transaction(
                user, new BigDecimal("1000"), today, "Today income",
                incomeCategory, TransactionType.INCOME));
        transactionRepository.save(new Transaction(
                user, new BigDecimal("50"), yesterday, "Yesterday expense",
                expenseCategory, TransactionType.EXPENSE));
        transactionRepository.save(new Transaction(
                user, new BigDecimal("200"), lastWeek, "Last week expense",
                expenseCategory, TransactionType.EXPENSE));

        // Test date range query
        List<Transaction> recentTransactions = transactionRepository
                .findByUserIdAndTransactionDateBetween(user.getId(), yesterday, today);
        assertThat(recentTransactions).hasSize(2);

        // Test total income query
        BigDecimal totalIncome = transactionRepository
                .getTotalIncomeByUserAndDateRange(user.getId(), lastWeek, today);
        assertThat(totalIncome).isEqualByComparingTo(new BigDecimal("1000"));

        // Test total expenses query
        BigDecimal totalExpenses = transactionRepository
                .getTotalExpensesByUserAndDateRange(user.getId(), lastWeek, today);
        assertThat(totalExpenses).isEqualByComparingTo(new BigDecimal("250"));
    }

    @Test
    void testJpaAuditingTimestamps() throws InterruptedException {
        // Create and save a user
        User user = new User("audit@example.com", "password");
        User savedUser = userRepository.save(user);

        // Verify timestamps are set
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isEqualTo(savedUser.getUpdatedAt());

        // Wait a bit to ensure timestamps differ
        Thread.sleep(100);

        // Update user
        savedUser.setEmail("updated@example.com");
        User updatedUser = userRepository.save(savedUser);

        // Verify updatedAt changed but createdAt didn't
        assertThat(updatedUser.getCreatedAt()).isEqualTo(savedUser.getCreatedAt());
        assertThat(updatedUser.getUpdatedAt()).isAfter(savedUser.getCreatedAt());
    }

    @Test
    void testCascadeDeleteOnUserDeletion() {
        // Create user with transactions
        User user = new User("cascade@example.com", "password");
        user = userRepository.save(user);

        Category category = categoryRepository.findAll().get(0);

        Transaction transaction = new Transaction(
                user,
                new BigDecimal("100"),
                LocalDate.now(),
                "Test",
                category,
                TransactionType.EXPENSE
        );
        transactionRepository.save(transaction);

        // Verify transaction exists
        List<Transaction> userTransactions = transactionRepository.findByUserId(user.getId());
        assertThat(userTransactions).hasSize(1);

        // Delete user
        userRepository.delete(user);

        // Verify transactions were cascaded deleted
        userTransactions = transactionRepository.findByUserId(user.getId());
        assertThat(userTransactions).isEmpty();
    }
}
