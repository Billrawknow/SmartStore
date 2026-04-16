package com.rwaknow.smartstore.GQL.modules.customer;

import com.rwaknow.smartstore.model.Order;
import com.rwaknow.smartstore.model.User;
import com.rwaknow.smartstore.repository.OrderRepository;
import com.rwaknow.smartstore.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Customer account queries - profile, orders, etc.
 */
@Controller("customerQueries")
@RequiredArgsConstructor
public class Queries {

    private final OrderRepository orderRepository;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public User myProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userDetails.getUser();
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Order> myRecentOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User currentUser = userDetails.getUser();
        return orderRepository.findTop10ByUserIdOrderByCreatedAtDesc(currentUser.getId());
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Long myOrderCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User currentUser = userDetails.getUser();
        return orderRepository.countByUserId(currentUser.getId());
    }
}