package com.github.ggruzdov.sqrddemo.response;

import com.github.ggruzdov.sqrddemo.model.Order;

import java.time.Instant;

public record SearchOrderResponse(
    Integer id,
    String customerFirstName,
    String customerLastName,
    String customerPhone,
    String deliveryAddress,
    Integer pilotes,
    String totalPrice,
    Instant createdAt
) {
    public static SearchOrderResponse from(Order order) {
        return new SearchOrderResponse(
            order.getId(),
            order.getCustomerFirstName(),
            order.getCustomerLastName(),
            order.getCustomerPhone(),
            order.getDeliveryAddress(),
            order.getPilotes(),
            String.format("%.2f", order.getTotalPrice() / 100.0),
            order.getCreatedAt()
        );
    }
}
