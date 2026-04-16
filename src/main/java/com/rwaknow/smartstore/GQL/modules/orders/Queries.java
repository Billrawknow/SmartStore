package com.rwaknow.smartstore.GQL.modules.orders;

import com.rwaknow.smartstore.GQL.modules.orders.Types.OrderPaginatedResult;
import com.rwaknow.smartstore.model.Order;
import com.rwaknow.smartstore.model.User;
import com.rwaknow.smartstore.security.CustomUserDetails;
import com.rwaknow.smartstore.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

/**
 * Order module queries
 */
@Controller("orderQueries")
@RequiredArgsConstructor
public class Queries {

    private final OrderService orderService;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Order order(
            @Argument Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User currentUser = userDetails.getUser();
        return orderService.getOrderById(id, currentUser);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public OrderPaginatedResult myOrders(
            @Argument Integer page,
            @Argument Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User currentUser = userDetails.getUser();
        Page<Order> result = orderService.getMyOrders(
                currentUser,
                PageRequest.of(page != null ? page : 0, size != null ? size : 10)
        );

        return new OrderPaginatedResult(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber()
        );
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public OrderPaginatedResult allOrders(
            @Argument Integer page,
            @Argument Integer size) {

        Page<Order> result = orderService.getAllOrders(
                PageRequest.of(page != null ? page : 0, size != null ? size : 10)
        );

        return new OrderPaginatedResult(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber()
        );
    }
}