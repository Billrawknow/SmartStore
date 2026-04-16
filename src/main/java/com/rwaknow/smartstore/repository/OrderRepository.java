package com.rwaknow.smartstore.repository;

import com.rwaknow.smartstore.model.Order;
import com.rwaknow.smartstore.model.OrderStatus;
import com.rwaknow.smartstore.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Existing methods
    Page<Order> findByUserId(Long userId, Pageable pageable);
    List<Order> findByUserId(Long userId);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);
    Optional<Order> findByMpesaReceiptNumber(String receiptNumber);

    // ─ Admin analytics ─
    long countByStatus(OrderStatus status);
    List<Order> findTop5ByOrderByCreatedAtDesc();
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);
}