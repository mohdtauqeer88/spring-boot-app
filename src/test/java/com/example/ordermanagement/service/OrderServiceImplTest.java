package com.example.ordermanagement.service;

import com.example.ordermanagement.domain.Order;
import com.example.ordermanagement.domain.OrderStatus;
import com.example.ordermanagement.dto.OrderCreateRequest;
import com.example.ordermanagement.dto.OrderResponse;
import com.example.ordermanagement.dto.OrderUpdateRequest;
import com.example.ordermanagement.exception.ResourceNotFoundException;
import com.example.ordermanagement.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_shouldPersistOrder() {
        var request = new OrderCreateRequest("CUST-100", new BigDecimal("150.00"), "USD");
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.createOrder(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.NEW);
        assertThat(response.getOrderNumber()).startsWith("ORD-");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getOrderById_shouldReturnOrderResponse() {
        UUID orderId = UUID.randomUUID();
        var order = Order.builder()
                .id(orderId)
                .orderNumber("ORD-100")
                .customerId("CUST-101")
                .orderStatus(OrderStatus.PROCESSING)
                .totalAmount(new BigDecimal("99.99"))
                .currency("USD")
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(orderId);

        assertThat(response.getId()).isEqualTo(orderId);
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    void getOrderById_whenNotFound_shouldThrow() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void searchOrders_shouldReturnPagedResults() {
        var order = Order.builder()
                .id(UUID.randomUUID())
                .orderNumber("ORD-200")
                .customerId("CUST-200")
                .orderStatus(OrderStatus.NEW)
                .totalAmount(new BigDecimal("120.00"))
                .currency("EUR")
                .build();

        when(orderRepository.findAllByOrderStatus(eq(OrderStatus.NEW), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(order)));

        var result = orderService.searchOrders(OrderStatus.NEW, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).extracting("orderNumber").containsExactly("ORD-200");
    }

    @Test
    void updateOrder_shouldModifyOrder() {
        UUID orderId = UUID.randomUUID();
        var existing = Order.builder()
                .id(orderId)
                .orderNumber("ORD-300")
                .customerId("CUST-300")
                .orderStatus(OrderStatus.NEW)
                .totalAmount(new BigDecimal("60.00"))
                .currency("USD")
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new OrderUpdateRequest("CUST-301", new BigDecimal("75.00"), "USD", OrderStatus.PROCESSING);
        var response = orderService.updateOrder(orderId, request);

        assertThat(response.getCustomerId()).isEqualTo("CUST-301");
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    void cancelOrder_shouldSetCancelledStatus() {
        UUID orderId = UUID.randomUUID();
        var existing = Order.builder()
                .id(orderId)
                .orderNumber("ORD-400")
                .customerId("CUST-400")
                .orderStatus(OrderStatus.PROCESSING)
                .totalAmount(new BigDecimal("45.00"))
                .currency("USD")
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.cancelOrder(orderId);

        assertThat(existing.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).save(existing);
    }
}