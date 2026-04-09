package com.example.ordermanagement.service;

import com.example.ordermanagement.domain.OrderStatus;
import com.example.ordermanagement.dto.OrderCreateRequest;
import com.example.ordermanagement.dto.OrderResponse;
import com.example.ordermanagement.dto.OrderUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(OrderCreateRequest request);

    OrderResponse getOrderById(UUID orderId);

    Page<OrderResponse> searchOrders(OrderStatus orderStatus, Pageable pageable);

    OrderResponse updateOrder(UUID orderId, OrderUpdateRequest request);

    void cancelOrder(UUID orderId);
}