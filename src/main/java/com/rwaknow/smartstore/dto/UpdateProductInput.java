package com.rwaknow.smartstore.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateProductInput {
    private String name;
    private String description;

    @Min(value = 0, message = "Price must be positive")
    private Double price;

    @Min(value = 0, message = "Stock must be non-negative")
    private Integer stock;

    private Long categoryId;
    private Boolean available;
}