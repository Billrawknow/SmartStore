package com.rwaknow.smartstore.graphql;

import com.rwaknow.smartstore.dto.CreateProductInput;
import com.rwaknow.smartstore.dto.ProductFilterInput;
import com.rwaknow.smartstore.dto.UpdateProductInput;
import com.rwaknow.smartstore.model.Product;
import com.rwaknow.smartstore.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductResolver {

    private final ProductService productService;

    @QueryMapping
    public Map<String, Object> products(@Argument Long categoryId,
                                        @Argument Double minPrice,
                                        @Argument Double maxPrice,
                                        @Argument String searchQuery,
                                        @Argument Boolean availableOnly,
                                        @Argument String sortBy,
                                        @Argument String sortOrder,
                                        @Argument Integer page,
                                        @Argument Integer size) {

        ProductFilterInput filter = new ProductFilterInput();
        filter.setCategoryId(categoryId);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setSearchQuery(searchQuery);
        filter.setAvailableOnly(availableOnly);
        filter.setSortBy(sortBy);
        filter.setSortOrder(sortOrder);
        filter.setPage(page != null ? page : 0);
        filter.setSize(size != null ? size : 20);

        Page<Product> productPage = productService.getProducts(filter);

        Map<String, Object> result = new HashMap<>();
        result.put("items", productPage.getContent());
        result.put("totalItems", productPage.getTotalElements());
        result.put("totalPages", productPage.getTotalPages());
        result.put("currentPage", productPage.getNumber());

        return result;
    }

    @QueryMapping
    public Product product(@Argument Long id) {
        return productService.getProductById(id);
    }

    @QueryMapping
    public List<Product> topRatedProducts() {
        return productService.getTopRatedProducts();
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(@Argument String name,
                                 @Argument String description,
                                 @Argument Double price,
                                 @Argument Integer stock,
                                 @Argument Long categoryId,
                                 @Argument Boolean available) {

        CreateProductInput input = new CreateProductInput();
        input.setName(name);
        input.setDescription(description);
        input.setPrice(price);
        input.setStock(stock);
        input.setCategoryId(categoryId);
        input.setAvailable(available != null ? available : true);

        return productService.createProduct(input);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateProduct(@Argument Long id,
                                 @Argument String name,
                                 @Argument String description,
                                 @Argument Double price,
                                 @Argument Integer stock,
                                 @Argument Long categoryId,
                                 @Argument Boolean available) {

        UpdateProductInput input = new UpdateProductInput();
        input.setName(name);
        input.setDescription(description);
        input.setPrice(price);
        input.setStock(stock);
        input.setCategoryId(categoryId);
        input.setAvailable(available);

        return productService.updateProduct(id, input);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteProduct(@Argument Long id) {
        productService.deleteProduct(id);
        return true;
    }
}