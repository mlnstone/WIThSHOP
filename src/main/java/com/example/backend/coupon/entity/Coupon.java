package com.example.backend.coupon.entity;

import com.example.backend.common.enums.CouponState;
import com.example.backend.common.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(nullable = false)
    private String couponName;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false)
    private Long discount;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType; // ★ 추가

    private Long limitQuantity;

    private Long minAmount;

    private LocalDateTime createdAt;

    private LocalDateTime limitAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponState state;

    public void update(
            String couponName,
            String code,
            DiscountType discountType,
            Long discount,
            Long limitQuantity,
            Long minAmount,
            LocalDateTime limitAt,
            CouponState state
    ) {
        this.couponName = couponName;
        this.code = code;
        this.discountType = discountType;
        this.discount = discount;
        this.limitQuantity = limitQuantity;
        this.minAmount = minAmount;
        this.limitAt = limitAt;
        this.state = state;
    }
}
