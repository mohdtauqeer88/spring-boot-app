package com.example.ordermanagement.dto;

import com.example.ordermanagement.domain.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequest {

    @NotBlank(message = "customerId is required")
    private String customerId;

    @NotNull(message = "totalAmount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "totalAmount must be greater than zero")
    private BigDecimal totalAmount;

    @NotBlank(message = "currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a valid ISO 4217 code")
    private String currency;

    @NotNull(message = "orderStatus is required")
    private OrderStatus orderStatus;
}