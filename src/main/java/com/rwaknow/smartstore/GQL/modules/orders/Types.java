package com.rwaknow.smartstore.GQL.modules.orders;

import com.rwaknow.smartstore.model.Order;
import java.util.List;

/**
 * Order module types
 */
public class Types {

    public record OrderPaginatedResult(
            List<Order> items,
            int totalItems,
            int totalPages,
            int currentPage
    ) {}

    public record MpesaPaymentResponse(
            boolean success,
            String checkoutRequestId,
            String merchantRequestId,
            String responseCode,
            String responseDescription,
            String message
    ) {}
}