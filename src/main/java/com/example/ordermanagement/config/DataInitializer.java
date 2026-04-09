package com.example.ordermanagement.config;

import com.example.ordermanagement.domain.Order;
import com.example.ordermanagement.domain.OrderStatus;
import com.example.ordermanagement.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Profile("!test")
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final OrderRepository orderRepository;

    public DataInitializer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        if (orderRepository.count() > 0) {
            log.info("Sample data already initialized");
            return;
        }

        var now = OffsetDateTime.now();

        List<Order> orders = List.of(
                Order.builder()
                        .id(UUID.randomUUID())
                        .orderNumber("ORD-20260408-0001")
                        .customerId("CUST-001")
                        .orderStatus(OrderStatus.NEW)
                        .totalAmount(new BigDecimal("250.00"))
                        .currency("USD")
                        .createdAt(now.minusDays(1))
                        .updatedAt(now.minusDays(1))
                        .build(),
                Order.builder()
                        .id(UUID.randomUUID())
                        .orderNumber("ORD-20260408-0002")
                        .customerId("CUST-002")
                        .orderStatus(OrderStatus.PROCESSING)
                        .totalAmount(new BigDecimal("540.50"))
                        .currency("EUR")
                        .createdAt(now.minusHours(8))
                        .updatedAt(now.minusHours(8))
                        .build(),
                Order.builder()
                        .id(UUID.randomUUID())
                        .orderNumber("ORD-20260408-0003")
                        .customerId("CUST-003")
                        .orderStatus(OrderStatus.COMPLETED)
                        .totalAmount(new BigDecimal("1120.99"))
                        .currency("USD")
                        .createdAt(now.minusDays(2))
                        .updatedAt(now.minusDays(1))
                        .build()
        );

        orderRepository.saveAll(orders);
        log.info("Initialized {} sample orders", orders.size());
    }
}