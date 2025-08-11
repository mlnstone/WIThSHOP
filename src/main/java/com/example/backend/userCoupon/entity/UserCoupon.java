package com.example.backend.userCoupon.entity;

import com.example.backend.common.enums.CouponStatus;
import com.example.backend.coupon.entity.Coupon;
import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserCoupon {

    @Id
    private String userCouponId;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus isUsed;

    public void markUsed() {
        if (this.isUsed == CouponStatus.USED) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        this.isUsed = CouponStatus.USED;
    }

    public void markUnused() {
        this.isUsed = CouponStatus.UNUSED;
    }
}
