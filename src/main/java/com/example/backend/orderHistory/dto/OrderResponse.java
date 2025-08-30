package com.example.backend.orderHistory.dto;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.orderHistory.entity.OrderHistory;
import com.example.backend.portOne.CashItem;
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
    private String impUid;

    public static OrderResponse from(OrderHistory order, CashItem cashItem, List<OrderItemResponse> items) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .orderPrice(order.getOrderPrice())
                .orderCreatedAt(order.getOrderCreatedAt())
                .orderStatus(order.getOrderStatus())
                .items(items)
                .impUid(cashItem != null ? cashItem.getImpUid() : null)
                .build();
    }
}