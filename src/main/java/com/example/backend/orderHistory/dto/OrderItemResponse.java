package com.example.backend.orderHistory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemResponse {
    private Long menuId;
    private String menuName;
    private Long price;     // 단가(주문 시점)
    private Long quantity;
    private Long lineTotal; // price * quantity
}