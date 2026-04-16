package com.rwaknow.smartstore.GQL.modules.products;

import com.rwaknow.smartstore.dto.CreateProductInput;
import com.rwaknow.smartstore.dto.UpdateProductInput;
import com.rwaknow.smartstore.model.Product;
import com.rwaknow.smartstore.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/**
 * Product module mutations — admin only
 */
@Controller("productMutations")
@RequiredArgsConstructor
public class Mutations {

    private final ProductService productService;

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(
            @Argument String name,
            @Argument String description,
            @Argument Double price,
            @Argument Integer stock,
            @Argument Long categoryId,
            @Argument Boolean available) {

        // Use setters - DTOs only have @Data (no all-args constructor)
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
    public Product updateProduct(
            @Argument Long id,
            @Argument String name,
            @Argument String description,
            @Argument Double price,
            @Argument Integer stock,
            @Argument Long categoryId,
            @Argument Boolean available) {

        // Use setters - DTOs only have @Data (no all-args constructor)
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