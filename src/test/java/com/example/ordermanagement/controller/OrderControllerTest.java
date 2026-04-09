package com.example.ordermanagement.controller;

import com.example.ordermanagement.domain.OrderStatus;
import com.example.ordermanagement.dto.OrderCreateRequest;
import com.example.ordermanagement.dto.OrderResponse;
import com.example.ordermanagement.dto.OrderUpdateRequest;
import com.example.ordermanagement.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_returnsCreatedResponse() throws Exception {
        var request = new OrderCreateRequest("CUST-001", new BigDecimal("123.45"), "USD");
        var response = OrderResponse.builder()
                .id(UUID.randomUUID())
                .orderNumber("ORD-123")
                .customerId("CUST-001")
                .orderStatus(OrderStatus.NEW)
                .totalAmount(new BigDecimal("123.45"))
                .currency("USD")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        Mockito.when(orderService.createOrder(Mockito.any(OrderCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/orders/" + response.getId())))
                .andExpect(jsonPath("$.orderNumber").value("ORD-123"))
                .andExpect(jsonPath("$.customerId").value("CUST-001"));
    }

    @Test
    void getOrderById_returnsOrder() throws Exception {
        UUID id = UUID.randomUUID();
        var response = OrderResponse.builder()
                .id(id)
                .orderNumber("ORD-200")
                .customerId("CUST-200")
                .orderStatus(OrderStatus.PROCESSING)
                .totalAmount(new BigDecimal("99.00"))
                .currency("USD")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        Mockito.when(orderService.getOrderById(id)).thenReturn(response);

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.orderNumber").value("ORD-200"));
    }

    @Test
    void searchOrders_returnsPagedResults() throws Exception {
        var response = OrderResponse.builder()
                .id(UUID.randomUUID())
                .orderNumber("ORD-300")
                .customerId("CUST-300")
                .orderStatus(OrderStatus.NEW)
                .totalAmount(new BigDecimal("45.00"))
                .currency("USD")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        Mockito.when(orderService.searchOrders(Mockito.eq(OrderStatus.NEW), Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/orders")
                        .param("status", "NEW")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD-300"));
    }

    @Test
    void updateOrder_returnsUpdatedOrder() throws Exception {
        UUID id = UUID.randomUUID();
        var request = new OrderUpdateRequest("CUST-400", new BigDecimal("150.00"), "USD", OrderStatus.PROCESSING);
        var response = OrderResponse.builder()
                .id(id)
                .orderNumber("ORD-400")
                .customerId("CUST-400")
                .orderStatus(OrderStatus.PROCESSING)
                .totalAmount(new BigDecimal("150.00"))
                .currency("USD")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        Mockito.when(orderService.updateOrder(Mockito.eq(id), Mockito.any(OrderUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/orders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("PROCESSING"));
    }

    @Test
    void cancelOrder_returnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(orderService).cancelOrder(id);

        mockMvc.perform(delete("/api/orders/{id}", id))
                .andExpect(status().isNoContent());
    }
}