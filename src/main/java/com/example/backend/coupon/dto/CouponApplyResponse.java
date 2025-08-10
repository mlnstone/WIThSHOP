package com.example.backend.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponApplyResponse {
    private Long orderAmount;     // 주문금액
    private Long discountApplied; // 적용된 할인금액
    private Long payAmount;       // 최종 결제금액 = orderAmount - discountApplied
    private String message;       // 왜 이 금액이 나왔는지 설명(필요시)
}