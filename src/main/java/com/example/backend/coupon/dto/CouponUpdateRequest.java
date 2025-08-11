package com.example.backend.coupon.dto;

import com.example.backend.common.enums.CouponState;
import com.example.backend.common.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponUpdateRequest {
    @NotBlank
    private String couponName;
    @NotBlank
    private String code;          // ← 코드 수정 허용할지 말지 결정
    @NotNull
    private DiscountType discountType;
    @NotNull
    private Long discount;
    private Long limitQuantity;
    private Long minAmount;
    private LocalDateTime limitAt;

    @NotNull
    private CouponState state;
}