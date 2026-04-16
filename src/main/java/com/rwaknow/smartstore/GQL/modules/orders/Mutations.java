package com.rwaknow.smartstore.GQL.modules.orders;

import com.rwaknow.smartstore.GQL.modules.orders.Types.MpesaPaymentResponse;
import com.rwaknow.smartstore.dto.CreateOrderInput;
import com.rwaknow.smartstore.dto.MpesaPaymentRequest;
import com.rwaknow.smartstore.dto.OrderItemInput;
import com.rwaknow.smartstore.dto.UpdateOrderStatusInput;
import com.rwaknow.smartstore.model.Order;
import com.rwaknow.smartstore.model.OrderStatus;
import com.rwaknow.smartstore.model.User;
import com.rwaknow.smartstore.security.CustomUserDetails;
import com.rwaknow.smartstore.services.MpesaService;
import com.rwaknow.smartstore.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * Order module mutations
 */
@Controller("orderMutations")
@RequiredArgsConstructor
public class Mutations {

    private final OrderService orderService;
    private final MpesaService mpesaService;

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Order createOrder(
            @Argument List<OrderItemInput> items,
            @Argument String shippingAddress,
            @Argument String phoneNumber,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User currentUser = userDetails.getUser();

        CreateOrderInput input = new CreateOrderInput();
        input.setItems(items);
        input.setShippingAddress(shippingAddress);
        input.setPhoneNumber(phoneNumber);

        return orderService.createOrder(input, currentUser);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Order updateOrderStatus(
            @Argument Long orderId,
            @Argument String status) {

        UpdateOrderStatusInput input = new UpdateOrderStatusInput();
        input.setOrderId(orderId);
        input.setStatus(OrderStatus.valueOf(status));

        return orderService.updateOrderStatus(input);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public MpesaPaymentResponse initiatePayment(
            @Argument Long orderId,
            @Argument String phoneNumber,
            @Argument Double amount) {

        // Create MpesaPaymentRequest DTO
        MpesaPaymentRequest request = new MpesaPaymentRequest();
        request.setPhoneNumber(phoneNumber);
        request.setAmount(amount);
        request.setOrderId(orderId);
        Map<String, Object> response = mpesaService.initiateStkPush(request);

        return new MpesaPaymentResponse(
                "0".equals(response.get("ResponseCode")),
                (String) response.get("CheckoutRequestID"),
                (String) response.get("MerchantRequestID"),
                (String) response.get("ResponseCode"),
                (String) response.get("ResponseDescription"),
                (String) response.get("CustomerMessage")
        );
    }
}