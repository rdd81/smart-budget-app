package com.smartbudget.dto;

import java.util.UUID;

/**
 * Represents the result of a categorization suggestion.
 */
public class CategorySuggestion {

    private UUID categoryId;
    private String categoryName;
    private double confidence;

    public CategorySuggestion() {
    }

    public CategorySuggestion(UUID categoryId, String categoryName, double confidence) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.confidence = confidence;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}

