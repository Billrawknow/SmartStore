package com.rwaknow.smartstore.repository;

import com.rwaknow.smartstore.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by category
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Find available products
    Page<Product> findByAvailableTrue(Pageable pageable);

    // Find products by category and availability
    Page<Product> findByCategoryIdAndAvailableTrue(Long categoryId, Pageable pageable);

    // Search products by name or description
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);

    // Find products by price range
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    // Find top-rated products
    List<Product> findTop10ByOrderByRatingDesc();
    // ── NEW: Admin analytics ─
    long countByStockLessThan(int stock);
    List<Product> findByStockLessThan(int stock);
    List<Product> findTop5ByOrderByCreatedAtDesc();
}