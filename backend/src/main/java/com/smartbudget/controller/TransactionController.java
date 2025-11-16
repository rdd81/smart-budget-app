package com.smartbudget.controller;

import com.smartbudget.dto.TransactionRequest;
import com.smartbudget.dto.TransactionResponse;
import com.smartbudget.exception.ErrorResponse;
import com.smartbudget.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller responsible for CRUD operations on transactions.
 */
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "CRUD operations for user transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "List transactions", description = "Returns a paginated list of transactions owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions retrieved", content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        UUID userId = extractUserId(authentication);
        Page<TransactionResponse> response = transactionService.getTransactions(userId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction", description = "Returns a single transaction if it belongs to the requesting user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction retrieved", content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransactionResponse> getTransaction(
            Authentication authentication,
            @PathVariable UUID id) {

        UUID userId = extractUserId(authentication);
        TransactionResponse response = transactionService.getTransaction(userId, id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create transaction", description = "Creates a new transaction tied to the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction created", content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransactionResponse> createTransaction(
            Authentication authentication,
            @Valid @RequestBody TransactionRequest request) {

        UUID userId = extractUserId(authentication);
        TransactionResponse response = transactionService.createTransaction(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update transaction", description = "Updates a transaction if owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction updated", content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransactionResponse> updateTransaction(
            Authentication authentication,
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request) {

        UUID userId = extractUserId(authentication);
        TransactionResponse response = transactionService.updateTransaction(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction", description = "Deletes a transaction if the authenticated user owns it.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transaction deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteTransaction(
            Authentication authentication,
            @PathVariable UUID id) {

        UUID userId = extractUserId(authentication);
        transactionService.deleteTransaction(userId, id);
        return ResponseEntity.noContent().build();
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
