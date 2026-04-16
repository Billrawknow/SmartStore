package com.rwaknow.smartstore.GQL.modules.products;

import com.rwaknow.smartstore.model.Product;
import java.util.List;

/**
 * Product module types
 */
public class Types {

    public record ProductFilterResult(
            List<Product> items,
            int totalItems,
            int totalPages,
            int currentPage
    ) {}
}