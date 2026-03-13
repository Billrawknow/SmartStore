package com.rwaknow.smartstore.service;

import com.rwaknow.smartstore.dto.CreateOrderInput;
import com.rwaknow.smartstore.dto.OrderItemInput;
import com.rwaknow.smartstore.dto.UpdateOrderStatusInput;
import com.rwaknow.smartstore.model.*;
import com.rwaknow.smartstore.repository.OrderRepository;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(CreateOrderInput input, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(input.getShippingAddress());
        order.setCustomerPhoneNumber(input.getPhoneNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        // Add order items
        for (OrderItemInput itemInput : input.getItems()) {
            Product product = productRepository.findById(itemInput.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemInput.getProductId()));

            // Check stock
            if (!product.isInStock(itemInput.getQuantity())) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemInput.getQuantity());
            orderItem.setPrice(product.getPrice());

            order.addItem(orderItem);

            // Reduce stock
            product.setStock(product.getStock() - itemInput.getQuantity());
            productRepository.save(product);
        }

        // Calculate total
        order.calculateTotal();

        return orderRepository.save(order);
    }

    public Page<Order> getUserOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderRepository.findByUserId(userId, pageable);
    }

    public Page<Order> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderRepository.findAll(pageable);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Transactional
    public Order updateOrderStatus(UpdateOrderStatusInput input) {
        Order order = getOrderById(input.getOrderId());
        order.setStatus(input.getStatus());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order updatePaymentStatus(Long orderId, PaymentStatus paymentStatus, String mpesaReceiptNumber) {
        Order order = getOrderById(orderId);
        order.setPaymentStatus(paymentStatus);
        order.setMpesaReceiptNumber(mpesaReceiptNumber);
        order.setUpdatedAt(LocalDateTime.now());

        if (paymentStatus == PaymentStatus.COMPLETED) {
            order.setStatus(OrderStatus.CONFIRMED);
        }

        return orderRepository.save(order);
    }
}