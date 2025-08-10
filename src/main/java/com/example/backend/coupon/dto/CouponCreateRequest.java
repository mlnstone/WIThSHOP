package com.example.backend.coupon.dto;

import com.example.backend.common.enums.DiscountType;
import com.example.backend.coupon.entity.Coupon;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponCreateRequest {
    @NotBlank
    private String couponName;

    @NotBlank
    private String code;

    @NotNull
    private DiscountType discountType; // ★ 추가: PERCENT | AMOUNT

    @Min(1)
    private Long discount;
    private Long limitQuantity;
    private Long minAmount;
    private LocalDateTime limitAt;

    public Coupon toEntity(LocalDateTime now) {
        return Coupon.builder()
                .couponName(couponName)
                .code(code)
                .discountType(discountType)
                .discount(discount)
                .limitQuantity(limitQuantity)
                .minAmount(minAmount)
                .createdAt(now)
                .limitAt(limitAt)
                .build();
    }
}
