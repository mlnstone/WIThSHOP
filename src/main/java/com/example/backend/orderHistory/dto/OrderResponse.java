// OrderResponse.java
package com.example.backend.orderHistory.dto;

import com.example.backend.common.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {
    private Long orderId;
    private String orderCode;
    private Long orderPrice;
    private LocalDateTime orderCreatedAt;
    private OrderStatus orderStatus;
    private List<OrderItemResponse> items;
}