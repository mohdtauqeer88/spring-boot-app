package com.example.ordermanagement.repository;

import com.example.ordermanagement.domain.Order;
import com.example.ordermanagement.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findAllByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    Optional<Order> findByOrderNumber(String orderNumber);
}