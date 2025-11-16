package com.smartbudget.dto;

import com.smartbudget.entity.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO representing system-wide categories exposed to clients.
 */
@Schema(description = "Category definition available to all users.")
public class CategoryResponse {

    @Schema(description = "Category identifier")
    private UUID id;

    @Schema(description = "Display name of the category")
    private String name;

    @Schema(description = "Category type", example = "EXPENSE")
    private CategoryType type;

    @Schema(description = "Additional description")
    private String description;

    public CategoryResponse() {
    }

    public CategoryResponse(UUID id, String name, CategoryType type, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
