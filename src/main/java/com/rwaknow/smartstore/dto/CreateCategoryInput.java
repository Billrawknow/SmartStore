package com.rwaknow.smartstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryInput {
    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
}