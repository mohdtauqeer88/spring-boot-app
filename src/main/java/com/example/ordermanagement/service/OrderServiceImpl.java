package com.example.ordermanagement.service;

import com.example.ordermanagement.domain.Order;
import com.example.ordermanagement.domain.OrderStatus;
import com.example.ordermanagement.dto.OrderCreateRequest;
import com.example.ordermanagement.dto.OrderMapper;
import com.example.ordermanagement.dto.OrderResponse;
import com.example.ordermanagement.dto.OrderUpdateRequest;
import com.example.ordermanagement.exception.BusinessException;
import com.example.ordermanagement.exception.ResourceNotFoundException;
import com.example.ordermanagement.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter ORDER_NUMBER_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        String orderNumber = generateOrderNumber();
        Order order = OrderMapper.toEntity(request, orderNumber);

        try {
            Order saved = orderRepository.save(order);
            log.info("Created order {} for customer {}", saved.getOrderNumber(), saved.getCustomerId());
            return OrderMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            log.error("Order creation failed for customer {}: {}", request.getCustomerId(), ex.getMessage());
            throw new BusinessException("Unable to create order. The order number may already exist.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(OrderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrders(OrderStatus orderStatus, Pageable pageable) {
        Page<Order> orders = orderStatus == null
                ? orderRepository.findAll(pageable)
                : orderRepository.findAllByOrderStatus(orderStatus, pageable);

        return orders.map(OrderMapper::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(UUID orderId, OrderUpdateRequest request) {
        Order existingOrder = resolveOrder(orderId);

        if (existingOrder.getOrderStatus() == OrderStatus.CANCELLED && request.getOrderStatus() != OrderStatus.CANCELLED) {
            throw new BusinessException("Cancelled orders cannot be modified");
        }

        existingOrder.setCustomerId(request.getCustomerId());
        existingOrder.setTotalAmount(request.getTotalAmount());
        existingOrder.setCurrency(request.getCurrency());
        existingOrder.setOrderStatus(request.getOrderStatus());

        Order updated = orderRepository.save(existingOrder);
        log.info("Updated order {} status {}", updated.getOrderNumber(), updated.getOrderStatus());
        return OrderMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId) {
        Order existingOrder = resolveOrder(orderId);

        if (existingOrder.getOrderStatus() == OrderStatus.CANCELLED) {
            log.debug("Order {} is already cancelled", existingOrder.getOrderNumber());
            return;
        }

        existingOrder.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(existingOrder);
        log.info("Cancelled order {}", existingOrder.getOrderNumber());
    }

    private Order resolveOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(ORDER_NUMBER_FORMATTER);
        int suffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD-" + timestamp + "-" + suffix;
    }
}
