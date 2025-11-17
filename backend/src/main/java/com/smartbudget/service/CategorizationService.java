package com.smartbudget.service;

import com.smartbudget.dto.CategorySuggestion;
import com.smartbudget.entity.CategorizationRule;
import com.smartbudget.entity.Category;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.repository.CategorizationRuleRepository;
import com.smartbudget.repository.CategoryRepository;
import com.smartbudget.repository.CategorizationFeedbackRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Provides keyword and heuristic-based category suggestions.
 */
@Service
public class CategorizationService {

    static final double EXACT_MATCH_CONFIDENCE = 0.9;
    static final double PARTIAL_MATCH_CONFIDENCE = 0.6;
    static final double AMOUNT_HEURISTIC_CONFIDENCE = 0.4;
    static final double MIN_CONFIDENCE_THRESHOLD = 0.3;
    static final double PERSONALIZED_CONFIDENCE = 0.95;
    static final long PERSONALIZATION_THRESHOLD = 3L;

    private static final BigDecimal LARGE_TRANSACTION_THRESHOLD = BigDecimal.valueOf(1000);
    private static final BigDecimal SMALL_TRANSACTION_THRESHOLD = BigDecimal.TEN;

    private final CategorizationRuleRepository ruleRepository;
    private final CategoryRepository categoryRepository;
    private final CategorizationFeedbackRepository feedbackRepository;

    public CategorizationService(CategorizationRuleRepository ruleRepository,
                                 CategoryRepository categoryRepository,
                                 CategorizationFeedbackRepository feedbackRepository) {
        this.ruleRepository = ruleRepository;
        this.categoryRepository = categoryRepository;
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Suggest a category for the provided description/amount combination.
     */
    @Transactional(readOnly = true)
    public CategorySuggestion suggestCategory(String description,
                                              BigDecimal amount,
                                              TransactionType transactionType) {
        return suggestCategory(description, amount, transactionType, null);
    }

    /**
     * Suggest a category for the provided description/amount combination with optional personalization.
     */
    @Transactional(readOnly = true)
    public CategorySuggestion suggestCategory(String description,
                                              BigDecimal amount,
                                              TransactionType transactionType,
                                              UUID userId) {
        if (transactionType == null) {
            return null;
        }

        Candidate bestCandidate = null;

        Candidate personalized = findPersonalizedCandidate(description, userId);
        bestCandidate = pickBetter(bestCandidate, personalized);

        String normalizedDescription = description == null ? "" : description.trim();
        String normalizedLower = normalizedDescription.toLowerCase(Locale.ENGLISH);
        if (!normalizedDescription.isEmpty()) {
            List<CategorizationRule> rules = ruleRepository.findByTransactionType(transactionType);
            for (CategorizationRule rule : rules) {
                Optional<Candidate> match = findRuleMatch(rule, description, normalizedLower);
                if (match.isPresent()) {
                    bestCandidate = pickBetter(bestCandidate, match.get());
                }
            }
        }

        Optional<Candidate> heuristicMatch = findAmountHeuristic(amount, transactionType);
        if (heuristicMatch.isPresent()) {
            bestCandidate = pickBetter(bestCandidate, heuristicMatch.get());
        }

        if (bestCandidate == null || bestCandidate.confidence() < MIN_CONFIDENCE_THRESHOLD) {
            return null;
        }

        Category category = bestCandidate.category();
        return new CategorySuggestion(category.getId(), category.getName(), bestCandidate.confidence());
    }

    private Optional<Candidate> findRuleMatch(CategorizationRule rule, String originalDescription, String normalizedLowerDescription) {
        if (rule.getKeyword() == null || rule.getKeyword().isBlank() || rule.getCategory() == null) {
            return Optional.empty();
        }

        String normalizedKeyword = rule.getKeyword().trim().toLowerCase(Locale.ENGLISH);
        if (normalizedKeyword.isEmpty()) {
            return Optional.empty();
        }

        if (containsWholeWord(originalDescription, normalizedKeyword)) {
            return Optional.of(new Candidate(rule.getCategory(), EXACT_MATCH_CONFIDENCE));
        }

        if (normalizedLowerDescription.contains(normalizedKeyword)) {
            return Optional.of(new Candidate(rule.getCategory(), PARTIAL_MATCH_CONFIDENCE));
        }

        return Optional.empty();
    }

    private Optional<Candidate> findAmountHeuristic(BigDecimal amount, TransactionType transactionType) {
        if (amount == null) {
            return Optional.empty();
        }

        if (transactionType == TransactionType.EXPENSE) {
            if (amount.compareTo(LARGE_TRANSACTION_THRESHOLD) > 0) {
                return materializeHeuristicCategory("Rent");
            }
            if (amount.compareTo(SMALL_TRANSACTION_THRESHOLD) < 0) {
                return materializeHeuristicCategory("Food")
                        .or(() -> materializeHeuristicCategory("Transport"));
            }
        } else if (transactionType == TransactionType.INCOME) {
            if (amount.compareTo(LARGE_TRANSACTION_THRESHOLD) > 0) {
                return materializeHeuristicCategory("Salary");
            }
            if (amount.compareTo(SMALL_TRANSACTION_THRESHOLD) < 0) {
                return materializeHeuristicCategory("Other")
                        .or(() -> materializeHeuristicCategory("Investments"));
            }
        }

        return Optional.empty();
    }

    private Optional<Candidate> materializeHeuristicCategory(String categoryName) {
        return categoryRepository.findFirstByNameIgnoreCase(categoryName)
                .map(category -> new Candidate(category, AMOUNT_HEURISTIC_CONFIDENCE));
    }

    private Candidate findPersonalizedCandidate(String description, UUID userId) {
        if (userId == null) {
            return null;
        }
        String token = extractToken(description);
        if (token.isBlank()) {
            return null;
        }
        Category category = getPersonalizedCategory(userId, token);
        if (category == null) {
            return null;
        }
        return new Candidate(category, PERSONALIZED_CONFIDENCE);
    }

    @Cacheable(value = "categorizationPersonalized", key = "T(String).format('%s:%s', #userId, #token)", unless = "#result == null")
    protected Category getPersonalizedCategory(UUID userId, String token) {
        return feedbackRepository.findTopCategoriesForUserAndToken(userId, token).stream()
                .filter(entry -> entry.getCorrectionCount() != null && entry.getCorrectionCount() >= PERSONALIZATION_THRESHOLD)
                .map(entry -> entry.getCategory())
                .findFirst()
                .orElse(null);
    }

    private String extractToken(String description) {
        if (description == null) {
            return "";
        }
        String[] parts = description.toLowerCase(Locale.ENGLISH).split("\\s+");
        for (String part : parts) {
            if (part.length() >= 3) {
                return part;
            }
        }
        return description.trim().toLowerCase(Locale.ENGLISH);
    }

    private boolean containsWholeWord(String text, String keyword) {
        if (text == null || text.isBlank()) {
            return false;
        }
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(text).find();
    }

    private Candidate pickBetter(Candidate current, Candidate next) {
        if (next == null) {
            return current;
        }
        if (current == null) {
            return next;
        }
        int confidenceComparison = Double.compare(next.confidence(), current.confidence());
        if (confidenceComparison > 0) {
            return next;
        }
        if (confidenceComparison < 0) {
            return current;
        }

        String currentName = current.category().getName();
        String nextName = next.category().getName();
        if (currentName == null) {
            return next;
        }
        if (nextName == null) {
            return current;
        }
        return currentName.compareToIgnoreCase(nextName) <= 0 ? current : next;
    }

    private record Candidate(Category category, double confidence) { }
}
