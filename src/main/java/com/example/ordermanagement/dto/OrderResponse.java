package com.example.ordermanagement.dto;

import com.example.ordermanagement.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private UUID id;
    private String orderNumber;
    private String customerId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private String currency;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}