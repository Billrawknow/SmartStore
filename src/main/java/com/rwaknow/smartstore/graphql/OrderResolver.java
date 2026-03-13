package com.rwaknow.smartstore.graphql;

import com.rwaknow.smartstore.dto.CreateOrderInput;
import com.rwaknow.smartstore.dto.MpesaPaymentRequest;
import com.rwaknow.smartstore.dto.OrderItemInput;
import com.rwaknow.smartstore.dto.UpdateOrderStatusInput;
import com.rwaknow.smartstore.model.Order;
import com.rwaknow.smartstore.model.OrderStatus;
import com.rwaknow.smartstore.model.User;
import com.rwaknow.smartstore.service.AuthService;
import com.rwaknow.smartstore.service.MpesaService;
import com.rwaknow.smartstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OrderResolver {

    private final OrderService orderService;
    private final AuthService authService;
    private final MpesaService mpesaService;

    @MutationMapping
    public Order createOrder(@Argument List<Map<String, Object>> items,
                             @Argument String shippingAddress,
                             @Argument String phoneNumber,
                             @AuthenticationPrincipal UserDetails userDetails) {

        User user = authService.getCurrentUser(userDetails.getUsername());

        CreateOrderInput input = new CreateOrderInput();
        input.setShippingAddress(shippingAddress);
        input.setPhoneNumber(phoneNumber);

        List<OrderItemInput> orderItems = items.stream().map(item -> {
            OrderItemInput orderItem = new OrderItemInput();
            orderItem.setProductId(((Number) item.get("productId")).longValue());
            orderItem.setQuantity((Integer) item.get("quantity"));
            return orderItem;
        }).toList();

        input.setItems(orderItems);

        return orderService.createOrder(input, user);
    }

    @QueryMapping
    public Map<String, Object> myOrders(@Argument Integer page,
                                        @Argument Integer size,
                                        @AuthenticationPrincipal UserDetails userDetails) {

        User user = authService.getCurrentUser(userDetails.getUsername());

        Page<Order> orderPage = orderService.getUserOrders(
                user.getId(),
                page != null ? page : 0,
                size != null ? size : 10
        );

        Map<String, Object> result = new HashMap<>();
        result.put("items", orderPage.getContent());
        result.put("totalItems", orderPage.getTotalElements());
        result.put("totalPages", orderPage.getTotalPages());
        result.put("currentPage", orderPage.getNumber());

        return result;
    }

    @QueryMapping
    public Order order(@Argument Long id) {
        return orderService.getOrderById(id);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> allOrders(@Argument Integer page, @Argument Integer size) {
        Page<Order> orderPage = orderService.getAllOrders(
                page != null ? page : 0,
                size != null ? size : 20
        );

        Map<String, Object> result = new HashMap<>();
        result.put("items", orderPage.getContent());
        result.put("totalItems", orderPage.getTotalElements());
        result.put("totalPages", orderPage.getTotalPages());
        result.put("currentPage", orderPage.getNumber());

        return result;
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Order updateOrderStatus(@Argument Long orderId, @Argument String status) {
        UpdateOrderStatusInput input = new UpdateOrderStatusInput();
        input.setOrderId(orderId);
        input.setStatus(OrderStatus.valueOf(status));

        return orderService.updateOrderStatus(input);
    }

    @MutationMapping
    public Map<String, Object> initiatePayment(@Argument Long orderId,
                                               @Argument String phoneNumber,
                                               @Argument Double amount) {

        MpesaPaymentRequest request = new MpesaPaymentRequest();
        request.setOrderId(orderId);
        request.setPhoneNumber(phoneNumber);
        request.setAmount(amount);

        return mpesaService.initiateStkPush(request);
    }
}