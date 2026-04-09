package com.example.ordermanagement.dto;

import com.example.ordermanagement.domain.Order;
import com.example.ordermanagement.domain.OrderStatus;

public final class OrderMapper {

    private OrderMapper() {
        // Utility class
    }

    public static OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public static Order toEntity(OrderCreateRequest request, String orderNumber) {
        return Order.builder()
                .orderNumber(orderNumber)
                .customerId(request.getCustomerId())
                .orderStatus(OrderStatus.NEW)
                .totalAmount(request.getTotalAmount())
                .currency(request.getCurrency())
                .build();
    }
}