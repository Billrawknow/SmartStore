package com.rwaknow.smartstore.GQL.modules.categories;

import com.rwaknow.smartstore.model.Category;
import com.rwaknow.smartstore.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Category module queries
 */
@Controller("categoryQueries")
@RequiredArgsConstructor
public class Queries {

    private final CategoryService categoryService;

    @QueryMapping
    public List<Category> categories() {
        return categoryService.getAllCategories();
    }

    @QueryMapping
    public Category category(@Argument Long id) {
        return categoryService.getCategoryById(id);
    }
}