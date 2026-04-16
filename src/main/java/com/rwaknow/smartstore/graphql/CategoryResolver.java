package com.rwaknow.smartstore.graphql;

import com.rwaknow.smartstore.dto.CreateCategoryInput;
import com.rwaknow.smartstore.model.Category;
import com.rwaknow.smartstore.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryResolver {

    private final CategoryService categoryService;

    @QueryMapping
    public List<Category> categories() {
        return categoryService.getAllCategories();
    }

    @QueryMapping
    public Category category(@Argument Long id) {
        return categoryService.getCategoryById(id);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Category createCategory(@Argument String name, @Argument String description) {
        CreateCategoryInput input = new CreateCategoryInput();
        input.setName(name);
        input.setDescription(description);

        return categoryService.createCategory(input);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteCategory(@Argument Long id) {
        categoryService.deleteCategory(id);
        return true;
    }
}