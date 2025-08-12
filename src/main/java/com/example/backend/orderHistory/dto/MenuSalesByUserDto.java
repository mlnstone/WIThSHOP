package com.example.backend.orderHistory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuSalesByUserDto {
    private Long userId;
    private String userName;
    private Long totalQuantity;   // 해당 메뉴 총 수량
    private Long totalAmount;     // 해당 메뉴 총 금액 (price * qty 합)
    private Long orderCount;      // 해당 메뉴가 포함된 주문 건수
}