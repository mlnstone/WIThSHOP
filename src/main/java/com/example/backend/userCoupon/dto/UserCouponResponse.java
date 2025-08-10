package com.example.backend.userCoupon.dto;

import com.example.backend.common.enums.CouponStatus;
import com.example.backend.common.enums.DiscountType;
import com.example.backend.userCoupon.entity.UserCoupon;

import java.time.LocalDateTime;

public record UserCouponResponse(
        String userCouponId,
        Long couponId,
        String couponName,
        String code,
        DiscountType discountType,
        Long discount,
        LocalDateTime limitAt,
        CouponStatus isUsed
) {
    public static UserCouponResponse from(UserCoupon uc) {
        var c = uc.getCoupon();
        return new UserCouponResponse(
                uc.getUserCouponId(),
                c.getCouponId(),
                c.getCouponName(),
                c.getCode(),
                c.getDiscountType(),
                c.getDiscount(),
                c.getLimitAt(),
                uc.getIsUsed()
        );
    }
}