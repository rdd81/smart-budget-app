package com.smartbudget.controller;

import com.smartbudget.dto.BulkCategorizationJobStatus;
import com.smartbudget.dto.BulkCategorizationRequest;
import com.smartbudget.service.BulkCategorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Bulk categorization operations")
public class BulkCategorizationController {

    private final BulkCategorizationService bulkCategorizationService;

    public BulkCategorizationController(BulkCategorizationService bulkCategorizationService) {
        this.bulkCategorizationService = bulkCategorizationService;
    }

    @PostMapping("/bulk-categorize")
    @Operation(summary = "Start bulk categorization job")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job started",
                    content = @Content(schema = @Schema(implementation = BulkCategorizationJobStatus.class)))
    })
    public ResponseEntity<BulkCategorizationJobStatus> startBulkCategorization(
            @Valid @RequestBody BulkCategorizationRequest request,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        BulkCategorizationJobStatus status = bulkCategorizationService.startJob(userId, request);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/bulk-categorize/{jobId}")
    @Operation(summary = "Get bulk categorization job status")
    public ResponseEntity<BulkCategorizationJobStatus> getJobStatus(@PathVariable UUID jobId) {
        BulkCategorizationJobStatus status = bulkCategorizationService.getJob(jobId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
}

