package com.smartbudget.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbudget.entity.Category;
import com.smartbudget.entity.CategoryType;
import com.smartbudget.entity.Transaction;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.entity.User;
import com.smartbudget.repository.CategoryRepository;
import com.smartbudget.repository.TransactionRepository;
import com.smartbudget.repository.UserRepository;
import com.smartbudget.service.JwtService;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AnalyticsControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private String token;
    private Category expenseCategory;
    private Category incomeCategory;

    @BeforeAll
    static void ensureDockerAvailable() {
        Assumptions.assumeTrue(isDockerAvailable(), "Docker is required for Testcontainers-based tests.");
    }

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        expenseCategory = categoryRepository.findByType(CategoryType.EXPENSE).get(0);
        incomeCategory = categoryRepository.findByType(CategoryType.INCOME).get(0);

        user = new User("analytics@example.com", passwordEncoder.encode("Password123!"));
        user = userRepository.save(user);
        token = jwtService.generateToken(user);
    }

    @Test
    void summaryEndpoint_ShouldReturnAggregatedValues() throws Exception {
        createTransaction(expenseCategory, TransactionType.EXPENSE, new BigDecimal("200"), LocalDate.now().minusDays(1));
        createTransaction(incomeCategory, TransactionType.INCOME, new BigDecimal("800"), LocalDate.now().minusDays(2));

        mockMvc.perform(get("/api/analytics/summary")
                        .header("Authorization", bearer(token))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(800.00))
                .andExpect(jsonPath("$.totalExpenses").value(200.00))
                .andExpect(jsonPath("$.balance").value(600.00))
                .andExpect(jsonPath("$.transactionCount").value(2));
    }

    @Test
    void categoryBreakdown_ShouldReturnIncomeAndExpenseCategories() throws Exception {
        createTransaction(expenseCategory, TransactionType.EXPENSE, new BigDecimal("120"), LocalDate.now().minusDays(1));
        createTransaction(expenseCategory, TransactionType.EXPENSE, new BigDecimal("30"), LocalDate.now().minusDays(1));
        createTransaction(incomeCategory, TransactionType.INCOME, new BigDecimal("400"), LocalDate.now().minusDays(2));

        mockMvc.perform(get("/api/analytics/category-breakdown")
                        .header("Authorization", bearer(token))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.transactionType=='EXPENSE')].transactionCount").value(List.of(2)))
                .andExpect(jsonPath("$[?(@.transactionType=='INCOME')].totalAmount").value(List.of(400.0)));
    }

    @Test
    void trendsEndpoint_ShouldReturnZeroFilledSeries() throws Exception {
        createTransaction(expenseCategory, TransactionType.EXPENSE, new BigDecimal("100"), LocalDate.of(2025, 1, 1));
        createTransaction(incomeCategory, TransactionType.INCOME, new BigDecimal("200"), LocalDate.of(2025, 1, 3));

        mockMvc.perform(get("/api/analytics/trends")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-03")
                        .param("groupBy", "DAY")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].totalExpenses").value(100.0))
                .andExpect(jsonPath("$[1].totalIncome").value(0.0))
                .andExpect(jsonPath("$[2].totalIncome").value(200.0));
    }

    private Transaction createTransaction(Category category, TransactionType type, BigDecimal amount, LocalDate date) {
        Transaction transaction = new Transaction(user, amount, date, category.getName(), category, type);
        return transactionRepository.save(transaction);
    }

    private static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
