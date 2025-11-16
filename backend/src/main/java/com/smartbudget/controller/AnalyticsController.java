package com.smartbudget.controller;

import com.smartbudget.dto.CategoryBreakdownResponse;
import com.smartbudget.dto.SummaryResponse;
import com.smartbudget.dto.TrendDataPoint;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Analytics endpoints for dashboard summaries.
 */
@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Dashboard analytics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary", description = "Returns aggregated totals for the specified date range.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SummaryResponse.class)))
    public ResponseEntity<SummaryResponse> getSummary(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        UUID userId = extractUserId(authentication);
        SummaryResponse response = analyticsService.getSummary(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category-breakdown")
    @Operation(summary = "Get category breakdown", description = "Returns aggregated totals per category for the specified date range.")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryBreakdownResponse.class))))
    public ResponseEntity<List<CategoryBreakdownResponse>> getCategoryBreakdown(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) TransactionType transactionType // optional to limit to a type
    ) {
        UUID userId = extractUserId(authentication);
        List<CategoryBreakdownResponse> allBreakdown = analyticsService.getCategoryBreakdown(userId, startDate, endDate);

        if (transactionType == null) {
            return ResponseEntity.ok(allBreakdown);
        }

        return ResponseEntity.ok(
                allBreakdown.stream()
                        .filter(response -> response.getTransactionType() == transactionType)
                .toList()
        );
    }

    @GetMapping("/trends")
    @Operation(summary = "Get income vs expenses trends", description = "Returns aggregated totals grouped by day/week/month.")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrendDataPoint.class))))
    public ResponseEntity<List<TrendDataPoint>> getTrends(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "MONTH") String groupBy) {
        UUID userId = extractUserId(authentication);
        List<TrendDataPoint> response = analyticsService.getTrends(userId, startDate, endDate, groupBy);
        return ResponseEntity.ok(response);
    }

    private UUID extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Missing authentication principal");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UUID uuid) {
            return uuid;
        }
        if (principal instanceof String stringPrincipal) {
            return UUID.fromString(stringPrincipal);
        }

        throw new IllegalStateException("Unsupported principal type");
    }
}
