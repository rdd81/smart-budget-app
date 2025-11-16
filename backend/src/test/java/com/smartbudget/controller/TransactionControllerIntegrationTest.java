package com.smartbudget.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbudget.dto.TransactionRequest;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests validating transaction endpoints with full security stack.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TransactionControllerIntegrationTest {

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
    private ObjectMapper objectMapper;

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

    private User primaryUser;
    private User secondaryUser;
    private Category defaultCategory;
    private String primaryToken;

    @BeforeAll
    static void ensureDockerAvailable() {
        Assumptions.assumeTrue(isDockerAvailable(), "Docker is required for Testcontainers-based tests.");
    }

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        List<Category> expenseCategories = categoryRepository.findByType(CategoryType.EXPENSE);
        assertThat(expenseCategories).isNotEmpty();
        defaultCategory = expenseCategories.get(0);

        primaryUser = new User("primary@example.com", passwordEncoder.encode("Password123!"));
        primaryUser = userRepository.save(primaryUser);

        secondaryUser = new User("secondary@example.com", passwordEncoder.encode("Password123!"));
        secondaryUser = userRepository.save(secondaryUser);

        primaryToken = jwtService.generateToken(primaryUser);
    }

    @Test
    void getTransactions_ShouldReturnPaginatedResultsForAuthenticatedUser() throws Exception {
        createTransaction(primaryUser, new BigDecimal("125.50"), LocalDate.now().minusDays(1), "First");
        createTransaction(primaryUser, new BigDecimal("250.00"), LocalDate.now(), "Second");
        createTransaction(secondaryUser, new BigDecimal("999.99"), LocalDate.now(), "Other user");

        mockMvc.perform(get("/api/transactions")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "amount")
                        .param("sortDirection", "desc")
                        .header("Authorization", bearer(primaryToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].amount").value(250.00))
                .andExpect(jsonPath("$.content[1].description").value("First"));
    }

    @Test
    void getTransaction_NotOwned_ShouldReturn403() throws Exception {
        Transaction otherUsersTransaction = createTransaction(secondaryUser, new BigDecimal("50.00"), LocalDate.now(), "Secret");

        mockMvc.perform(get("/api/transactions/{id}", otherUsersTransaction.getId())
                        .header("Authorization", bearer(primaryToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You do not have access to this transaction"));
    }

    @Test
    void createTransaction_ShouldPersistEntity() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("75.25"));
        request.setTransactionDate(LocalDate.now());
        request.setDescription("Test creation");
        request.setCategoryId(defaultCategory.getId());
        request.setTransactionType(TransactionType.EXPENSE);

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", bearer(primaryToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(75.25))
                .andExpect(jsonPath("$.category.id").value(defaultCategory.getId().toString()));

        assertThat(transactionRepository.findByUserId(primaryUser.getId())).hasSize(1);
    }

    @Test
    void updateTransaction_ShouldReturnUpdatedPayload() throws Exception {
        Transaction existing = createTransaction(primaryUser, new BigDecimal("10.00"), LocalDate.now().minusDays(2), "Coffee");

        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("19.95"));
        request.setTransactionDate(LocalDate.now());
        request.setDescription("Updated Coffee");
        request.setCategoryId(defaultCategory.getId());
        request.setTransactionType(TransactionType.EXPENSE);

        mockMvc.perform(put("/api/transactions/{id}", existing.getId())
                        .header("Authorization", bearer(primaryToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(19.95))
                .andExpect(jsonPath("$.description").value("Updated Coffee"));

        Transaction reloaded = transactionRepository.findById(existing.getId()).orElseThrow();
        assertThat(reloaded.getAmount()).isEqualByComparingTo("19.95");
        assertThat(reloaded.getDescription()).isEqualTo("Updated Coffee");
    }

    @Test
    void deleteTransaction_ShouldRemoveRecord() throws Exception {
        Transaction existing = createTransaction(primaryUser, new BigDecimal("42.00"), LocalDate.now().minusDays(3), "To remove");

        mockMvc.perform(delete("/api/transactions/{id}", existing.getId())
                        .header("Authorization", bearer(primaryToken)))
                .andExpect(status().isNoContent());

        assertThat(transactionRepository.existsById(existing.getId())).isFalse();
    }

    @Test
    void getTransaction_NotFound_ShouldReturn404() throws Exception {
        UUID unknownId = UUID.randomUUID();

        mockMvc.perform(get("/api/transactions/{id}", unknownId)
                        .header("Authorization", bearer(primaryToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Transaction not found"));
    }

    private Transaction createTransaction(User owner, BigDecimal amount, LocalDate date, String description) {
        Transaction transaction = new Transaction(owner, amount, date, description, defaultCategory, TransactionType.EXPENSE);
        return transactionRepository.save(transaction);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }
}
