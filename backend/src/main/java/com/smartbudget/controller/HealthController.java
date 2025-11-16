package com.smartbudget.controller;

import com.smartbudget.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;

/**
 * Controller for application health check endpoint.
 * Provides basic health status and connectivity checks for monitoring.
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Application health check endpoints")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    /**
     * Health check endpoint that verifies application and database status.
     *
     * @return HealthResponse with status, timestamp, version, and database connectivity
     */
    @GetMapping
    @Operation(
            summary = "Check application health",
            description = "Returns application health status including database connectivity check"
    )
    @ApiResponse(responseCode = "200", description = "Application is healthy")
    @ApiResponse(responseCode = "503", description = "Application is unhealthy")
    public ResponseEntity<HealthResponse> health() {
        String databaseStatus = checkDatabaseConnection();
        String overallStatus = "UP".equals(databaseStatus) ? "UP" : "DOWN";

        HealthResponse response = new HealthResponse(
                overallStatus,
                Instant.now(),
                "1.0.0",
                databaseStatus
        );

        if ("UP".equals(overallStatus)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(503).body(response);
        }
    }

    /**
     * Checks database connectivity by attempting to get a connection.
     *
     * @return "UP" if connection successful, "DOWN" if connection failed
     */
    private String checkDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return connection != null && !connection.isClosed() ? "UP" : "DOWN";
        } catch (Exception e) {
            return "DOWN";
        }
    }
}
