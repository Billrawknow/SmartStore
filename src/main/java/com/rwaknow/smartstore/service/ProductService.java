package com.rwaknow.smartstore.service;

import com.rwaknow.smartstore.dto.CreateProductInput;
import com.rwaknow.smartstore.dto.ProductFilterInput;
import com.rwaknow.smartstore.dto.UpdateProductInput;
import com.rwaknow.smartstore.model.Category;
import com.rwaknow.smartstore.model.Product;
import com.rwaknow.smartstore.repository.CategoryRepository;
import com.rwaknow.smartstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Product createProduct(CreateProductInput input) {
        Category category = categoryRepository.findById(input.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + input.getCategoryId()));

        Product product = new Product();
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setPrice(input.getPrice());
        product.setStock(input.getStock());
        product.setCategory(category);
        product.setAvailable(input.getAvailable());
        product.setCreatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, UpdateProductInput input) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (input.getName() != null) {
            product.setName(input.getName());
        }
        if (input.getDescription() != null) {
            product.setDescription(input.getDescription());
        }
        if (input.getPrice() != null) {
            product.setPrice(input.getPrice());
        }
        if (input.getStock() != null) {
            product.setStock(input.getStock());
        }
        if (input.getCategoryId() != null) {
            Category category = categoryRepository.findById(input.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + input.getCategoryId()));
            product.setCategory(category);
        }
        if (input.getAvailable() != null) {
            product.setAvailable(input.getAvailable());
        }

        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    public Page<Product> getProducts(ProductFilterInput filter) {
        Pageable pageable = createPageable(filter);

        if (filter.getSearchQuery() != null && !filter.getSearchQuery().isEmpty()) {
            return productRepository.searchProducts(filter.getSearchQuery(), pageable);
        }

        if (filter.getCategoryId() != null) {
            if (Boolean.TRUE.equals(filter.getAvailableOnly())) {
                return productRepository.findByCategoryIdAndAvailableTrue(filter.getCategoryId(), pageable);
            }
            return productRepository.findByCategoryId(filter.getCategoryId(), pageable);
        }

        if (filter.getMinPrice() != null && filter.getMaxPrice() != null) {
            return productRepository.findByPriceBetween(filter.getMinPrice(), filter.getMaxPrice(), pageable);
        }

        if (Boolean.TRUE.equals(filter.getAvailableOnly())) {
            return productRepository.findByAvailableTrue(pageable);
        }

        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<Product> getTopRatedProducts() {
        return productRepository.findTop10ByOrderByRatingDesc();
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    private Pageable createPageable(ProductFilterInput filter) {
        Sort sort = Sort.unsorted();

        if (filter.getSortBy() != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            switch (filter.getSortBy().toLowerCase()) {
                case "price":
                    sort = Sort.by(direction, "price");
                    break;
                case "name":
                    sort = Sort.by(direction, "name");
                    break;
                case "rating":
                    sort = Sort.by(direction, "rating");
                    break;
                case "createdat":
                    sort = Sort.by(direction, "createdAt");
                    break;
                default:
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
            }
        }

        return PageRequest.of(filter.getPage(), filter.getSize(), sort);
    }
}