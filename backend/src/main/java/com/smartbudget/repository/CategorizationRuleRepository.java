package com.smartbudget.repository;

import com.smartbudget.entity.CategorizationRule;
import com.smartbudget.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for keyword-based categorization rules.
 */
public interface CategorizationRuleRepository extends JpaRepository<CategorizationRule, Long> {

    /**
     * Retrieve all rules for the supplied transaction type.
     *
     * @param transactionType transaction type filter
     * @return list of rules
     */
    List<CategorizationRule> findByTransactionType(TransactionType transactionType);
}

