package com.smartbudget.dto;

import java.util.UUID;

/**
 * Represents the status of a bulk categorization job.
 */
public class BulkCategorizationJobStatus {

    public enum Status { PENDING, RUNNING, COMPLETED, FAILED }

    private final UUID jobId;
    private Status status;
    private long totalProcessed;
    private long totalUpdated;
    private long totalSkippedLowConfidence;
    private String error;

    public BulkCategorizationJobStatus(UUID jobId, Status status) {
        this.jobId = jobId;
        this.status = status;
    }

    public UUID getJobId() {
        return jobId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getTotalProcessed() {
        return totalProcessed;
    }

    public void setTotalProcessed(long totalProcessed) {
        this.totalProcessed = totalProcessed;
    }

    public long getTotalUpdated() {
        return totalUpdated;
    }

    public void setTotalUpdated(long totalUpdated) {
        this.totalUpdated = totalUpdated;
    }

    public long getTotalSkippedLowConfidence() {
        return totalSkippedLowConfidence;
    }

    public void setTotalSkippedLowConfidence(long totalSkippedLowConfidence) {
        this.totalSkippedLowConfidence = totalSkippedLowConfidence;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

