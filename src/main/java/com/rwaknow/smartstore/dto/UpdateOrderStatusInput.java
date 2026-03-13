package com.rwaknow.smartstore.dto;

import com.rwaknow.smartstore.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusInput {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Status is required")
    private OrderStatus status;
}