package com.smartbudget.service;

import com.smartbudget.dto.CategorySuggestion;
import com.smartbudget.entity.CategorizationRule;
import com.smartbudget.entity.Category;
import com.smartbudget.entity.CategoryType;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.repository.CategorizationRuleRepository;
import com.smartbudget.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorizationServiceTest {

    @Mock
    private CategorizationRuleRepository ruleRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategorizationService categorizationService;

    private Category food;
    private Category rent;
    private Category shopping;

    @BeforeEach
    void setUp() {
        food = createCategory("Food", CategoryType.EXPENSE);
        rent = createCategory("Rent", CategoryType.EXPENSE);
        shopping = createCategory("Shopping", CategoryType.EXPENSE);
    }

    @Test
    void suggestCategory_ShouldReturnExactMatch() {
        CategorizationRule rule = createRule(food, "coffee", TransactionType.EXPENSE);
        when(ruleRepository.findByTransactionType(TransactionType.EXPENSE)).thenReturn(List.of(rule));

        CategorySuggestion suggestion = categorizationService.suggestCategory(
                "Morning coffee at Starbucks", new BigDecimal("8.50"), TransactionType.EXPENSE);

        assertThat(suggestion).isNotNull();
        assertThat(suggestion.getCategoryId()).isEqualTo(food.getId());
        assertThat(suggestion.getConfidence()).isEqualTo(CategorizationService.EXACT_MATCH_CONFIDENCE);
    }

    @Test
    void suggestCategory_ShouldReturnPartialMatchWhenEmbedded() {
        CategorizationRule rule = createRule(rent, "rent", TransactionType.EXPENSE);
        when(ruleRepository.findByTransactionType(TransactionType.EXPENSE)).thenReturn(List.of(rule));

        CategorySuggestion suggestion = categorizationService.suggestCategory(
                "Rental fees due", BigDecimal.valueOf(900), TransactionType.EXPENSE);

        assertThat(suggestion).isNotNull();
        assertThat(suggestion.getCategoryId()).isEqualTo(rent.getId());
        assertThat(suggestion.getConfidence()).isEqualTo(CategorizationService.PARTIAL_MATCH_CONFIDENCE);
    }

    @Test
    void suggestCategory_ShouldUseLargeAmountHeuristicForExpense() {
        when(ruleRepository.findByTransactionType(TransactionType.EXPENSE)).thenReturn(Collections.emptyList());
        when(categoryRepository.findFirstByNameIgnoreCase("Rent")).thenReturn(Optional.of(rent));

        CategorySuggestion suggestion = categorizationService.suggestCategory(
                "Monthly payment", BigDecimal.valueOf(2000), TransactionType.EXPENSE);

        assertThat(suggestion).isNotNull();
        assertThat(suggestion.getCategoryId()).isEqualTo(rent.getId());
        assertThat(suggestion.getConfidence()).isEqualTo(CategorizationService.AMOUNT_HEURISTIC_CONFIDENCE);
    }

    @Test
    void suggestCategory_ShouldChooseHighestConfidenceMatch() {
        CategorizationRule partialRule = createRule(shopping, "shop", TransactionType.EXPENSE);
        CategorizationRule exactRule = createRule(food, "groceries", TransactionType.EXPENSE);
        when(ruleRepository.findByTransactionType(TransactionType.EXPENSE)).thenReturn(List.of(partialRule, exactRule));

        CategorySuggestion suggestion = categorizationService.suggestCategory(
                "Weekly groceries from local shop", null, TransactionType.EXPENSE);

        assertThat(suggestion).isNotNull();
        assertThat(suggestion.getCategoryId()).isEqualTo(food.getId());
        assertThat(suggestion.getConfidence()).isEqualTo(CategorizationService.EXACT_MATCH_CONFIDENCE);
    }

    @Test
    void suggestCategory_WithNoMatches_ShouldReturnNull() {
        when(ruleRepository.findByTransactionType(TransactionType.EXPENSE)).thenReturn(Collections.emptyList());

        CategorySuggestion suggestion = categorizationService.suggestCategory(
                "Unrecognized description", BigDecimal.valueOf(20), TransactionType.EXPENSE);

        assertThat(suggestion).isNull();
    }

    @Test
    void suggestCategory_SmallExpenseAmountPrefersFood() {
        when(ruleRepository.findByTransactionType(TransactionType.EXPENSE)).thenReturn(Collections.emptyList());
        when(categoryRepository.findFirstByNameIgnoreCase("Food")).thenReturn(Optional.of(food));

        CategorySuggestion suggestion = categorizationService.suggestCategory(
                "No keywords", BigDecimal.valueOf(5), TransactionType.EXPENSE);

        assertThat(suggestion).isNotNull();
        assertThat(suggestion.getCategoryId()).isEqualTo(food.getId());
        assertThat(suggestion.getConfidence()).isEqualTo(CategorizationService.AMOUNT_HEURISTIC_CONFIDENCE);
    }

    private Category createCategory(String name, CategoryType type) {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(name);
        category.setType(type);
        return category;
    }

    private CategorizationRule createRule(Category category, String keyword, TransactionType transactionType) {
        CategorizationRule rule = new CategorizationRule();
        rule.setCategory(category);
        rule.setKeyword(keyword);
        rule.setTransactionType(transactionType);
        return rule;
    }
}
