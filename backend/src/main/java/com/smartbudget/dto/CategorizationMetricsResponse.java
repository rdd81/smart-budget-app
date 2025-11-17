package com.smartbudget.dto;

import java.util.List;
import java.util.UUID;

public class CategorizationMetricsResponse {

    private long totalSuggestions;
    private long acceptedSuggestions;
    private long rejectedSuggestions;
    private double accuracy;
    private List<CategoryBreakdown> breakdown;

    public CategorizationMetricsResponse(long totalSuggestions, long acceptedSuggestions, long rejectedSuggestions, double accuracy, List<CategoryBreakdown> breakdown) {
        this.totalSuggestions = totalSuggestions;
        this.acceptedSuggestions = acceptedSuggestions;
        this.rejectedSuggestions = rejectedSuggestions;
        this.accuracy = accuracy;
        this.breakdown = breakdown;
    }

    public long getTotalSuggestions() {
        return totalSuggestions;
    }

    public long getAcceptedSuggestions() {
        return acceptedSuggestions;
    }

    public long getRejectedSuggestions() {
        return rejectedSuggestions;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public List<CategoryBreakdown> getBreakdown() {
        return breakdown;
    }

    public static class CategoryBreakdown {
        private UUID categoryId;
        private String categoryName;
        private long total;
        private long accepted;
        private long rejected;
        private double accuracy;

        public CategoryBreakdown(UUID categoryId, String categoryName, long total, long accepted, long rejected, double accuracy) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.total = total;
            this.accepted = accepted;
            this.rejected = rejected;
            this.accuracy = accuracy;
        }

        public UUID getCategoryId() {
            return categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public long getTotal() {
            return total;
        }

        public long getAccepted() {
            return accepted;
        }

        public long getRejected() {
            return rejected;
        }

        public double getAccuracy() {
            return accuracy;
        }
    }
}

