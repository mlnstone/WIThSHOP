package com.example.backend.coupon.dto;

import com.example.backend.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// CouponResponse.java
@Getter
@AllArgsConstructor
public class CouponResponse {
    private Long couponId;
    private String couponName;
    private String code;
    private Long discount;
    private Long limitQuantity;
    private Long minAmount;
    private LocalDateTime createdAt;
    private LocalDateTime limitAt;

    public static CouponResponse from(Coupon c) {
        return new CouponResponse(
                c.getCouponId(),
                c.getCouponName(),
                c.getCode(),
                c.getDiscount(),
                c.getLimitQuantity(),
                c.getMinAmount(),
                c.getCreatedAt(),
                c.getLimitAt()
        );
    }
}