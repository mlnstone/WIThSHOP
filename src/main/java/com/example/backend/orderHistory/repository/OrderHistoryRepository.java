package com.example.backend.orderHistory.repository;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.orderHistory.entity.OrderHistory;
import com.example.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    Page<OrderHistory> findByUser(User user, Pageable pageable);

    Page<OrderHistory> findByUser_UserId(Long userId, Pageable pageable);

    Page<OrderHistory> findByOrderStatus(OrderStatus status, Pageable pageable);

    Page<OrderHistory> findByUser_UserIdAndOrderStatus(Long userId, OrderStatus status, Pageable pageable);

    Optional<OrderHistory> findByOrderCodeAndUser(String orderCode, User user);

}