package com.smartbudget.controller;

import com.smartbudget.dto.CategorySuggestion;
import com.smartbudget.dto.CategorySuggestionRequest;
import com.smartbudget.dto.CategorySuggestionResponse;
import com.smartbudget.service.CategorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public API for transaction category suggestions.
 */
@RestController
@RequestMapping("/api/categorization")
@Tag(name = "Categorization", description = "Suggest categories for transactions via keyword rules")
public class CategorizationController {

    private final CategorizationService categorizationService;

    public CategorizationController(CategorizationService categorizationService) {
        this.categorizationService = categorizationService;
    }

    @PostMapping(value = "/suggest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Suggest a category",
            description = "Analyzes description and amount to propose the most likely category.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Transaction data used to infer category",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategorySuggestionRequest.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suggestion generated",
                    content = @Content(schema = @Schema(implementation = CategorySuggestionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content)
    })
    public ResponseEntity<CategorySuggestionResponse> suggestCategory(
            @Valid @RequestBody CategorySuggestionRequest request) {

        CategorySuggestion suggestion = categorizationService.suggestCategory(
                request.getDescription(),
                request.getAmount(),
                request.getTransactionType(),
                request.getUserId());

        return ResponseEntity.ok(CategorySuggestionResponse.fromSuggestion(suggestion));
    }
}
