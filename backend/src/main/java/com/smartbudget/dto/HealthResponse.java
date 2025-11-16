package com.smartbudget.dto;

import java.time.Instant;

/**
 * Response DTO for health check endpoint.
 */
public class HealthResponse {
    private String status;
    private Instant timestamp;
    private String version;
    private String database;

    public HealthResponse() {
    }

    public HealthResponse(String status, Instant timestamp, String version, String database) {
        this.status = status;
        this.timestamp = timestamp;
        this.version = version;
        this.database = database;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
