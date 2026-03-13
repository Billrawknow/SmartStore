package com.rwaknow.smartstore.dto;

import lombok.Data;

@Data
public class ProductFilterInput {
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
    private String searchQuery;
    private Boolean availableOnly;
    private String sortBy; // "price", "name", "rating", "createdAt"
    private String sortOrder; // "asc", "desc"
    private Integer page = 0;
    private Integer size = 20;
}