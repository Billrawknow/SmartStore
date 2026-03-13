package com.rwaknow.smartstore.repository;

import com.rwaknow.smartstore.model.Order;
import com.rwaknow.smartstore.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find orders by user
    Page<Order> findByUserId(Long userId, Pageable pageable);
    List<Order> findByUserId(Long userId);

    // Find orders by status
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // Find orders by user and status
    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    // Find order by M-Pesa receipt number
    Optional<Order> findByMpesaReceiptNumber(String receiptNumber);
}