package com.rwaknow.smartstore.GQL.modules.categories;

import com.rwaknow.smartstore.model.Category;
import com.rwaknow.smartstore.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller("categoryMutations")
@RequiredArgsConstructor
public class Mutations {

    private final CategoryService categoryService;

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Category createCategory(
            @Argument String name,
            @Argument String description) {

        return categoryService.createCategory(name, description);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteCategory(@Argument Long id) {
        categoryService.deleteCategory(id);
        return true;
    }
}