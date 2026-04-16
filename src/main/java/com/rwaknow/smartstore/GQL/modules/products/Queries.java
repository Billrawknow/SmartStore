package com.rwaknow.smartstore.GQL.modules.products;

import com.rwaknow.smartstore.GQL.modules.products.Types.ProductFilterResult;
import com.rwaknow.smartstore.model.Product;
import com.rwaknow.smartstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Product module queries
 */
@Controller("productQueries")
@RequiredArgsConstructor
public class Queries {

    private final ProductRepository productRepository;

    @QueryMapping
    public ProductFilterResult products(
            @Argument Long categoryId,
            @Argument Double minPrice,
            @Argument Double maxPrice,
            @Argument String searchQuery,
            @Argument Boolean availableOnly,
            @Argument String sortBy,
            @Argument String sortOrder,
            @Argument Integer page,
            @Argument Integer size) {

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sortBy != null) {
            Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder)
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            sort = Sort.by(direction, sortBy);
        }

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, sort);
        Page<Product> result;

        // Apply filters - use repository methods directly
        if (categoryId != null && Boolean.TRUE.equals(availableOnly)) {
            result = productRepository.findByCategoryIdAndAvailableTrue(categoryId, pageRequest);
        } else if (categoryId != null) {
            result = productRepository.findByCategoryId(categoryId, pageRequest);
        } else if (Boolean.TRUE.equals(availableOnly)) {
            result = productRepository.findByAvailableTrue(pageRequest);
        } else if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            result = productRepository.searchProducts(searchQuery, pageRequest);
        } else if (minPrice != null && maxPrice != null) {
            result = productRepository.findByPriceBetween(minPrice, maxPrice, pageRequest);
        } else {
            result = productRepository.findAll(pageRequest);
        }

        return new ProductFilterResult(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber()
        );
    }

    @QueryMapping
    public Product product(@Argument Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @QueryMapping
    public List<Product> topRatedProducts() {
        return productRepository.findTop10ByOrderByRatingDesc();
    }
}