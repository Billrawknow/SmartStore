package com.rwaknow.smartstore.services;

import com.rwaknow.smartstore.dto.CreateCategoryInput;
import com.rwaknow.smartstore.model.Category;
import com.rwaknow.smartstore.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(CreateCategoryInput input) {
        if (categoryRepository.existsByName(input.getName())) {
            throw new RuntimeException("Category with name '" + input.getName() + "' already exists");
        }

        Category category = new Category();
        category.setName(input.getName());
        category.setDescription(input.getDescription());

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
    /**
     * Wrapper method for GraphQL - creates category from individual fields
     */
    public Category createCategory(String name, String description) {
        CreateCategoryInput input = new CreateCategoryInput();
        input.setName(name);
        input.setDescription(description);
        return createCategory(input);
    }
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}