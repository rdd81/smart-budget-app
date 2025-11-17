package com.smartbudget.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * API response for category suggestion requests.
 */
public class CategorySuggestionResponse {

    @Schema(description = "Selected category identifier if available")
    private UUID categoryId;

    @Schema(description = "Suggested category name if available")
    private String categoryName;

    @Schema(description = "Confidence score between 0 and 1")
    private double confidence;

    public CategorySuggestionResponse() {
    }

    public CategorySuggestionResponse(UUID categoryId, String categoryName, double confidence) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.confidence = confidence;
    }

    public static CategorySuggestionResponse fromSuggestion(CategorySuggestion suggestion) {
        if (suggestion == null) {
            return new CategorySuggestionResponse(null, null, 0.0);
        }
        return new CategorySuggestionResponse(suggestion.getCategoryId(), suggestion.getCategoryName(), suggestion.getConfidence());
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

