package com.example.backend.portOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
class CheckoutSnapshot {
    private Long userId;
    private long subtotal;
    private long discountCoupon;
    private long discountPoints;
    private long shippingFee;
    private long finalAmount;     // = subtotal - coupon - points + shipping
    private String userCouponId;  // null 가능
}