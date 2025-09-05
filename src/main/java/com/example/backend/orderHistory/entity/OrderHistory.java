package com.example.backend.orderHistory.entity;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String orderCode;  // 외부 노출용 UUID

    @Column(nullable = false)
    private Long orderPrice;   // 최종 결제금액 (Subtotal - Coupon - Points + Shipping)

    @Column(nullable = false)
    private LocalDateTime orderCreatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ==========================
    // 결제/혜택 스냅샷 필드
    // ==========================
    @Column(nullable = false)
    private Long subtotal;        // 상품합계

    @Column(nullable = false)
    private Long discountCoupon;  // 쿠폰 할인

    @Column(nullable = false)
    private Long discountPoints;  // 포인트 사용

    @Column(nullable = false)
    private Long shippingFee;     // 배송비

    @Column(length = 64)
    private String userCouponId;  // 사용 쿠폰ID(없으면 null)

    @Column(nullable = false)
    private Boolean benefitsReverted; // 환불 시 복구완료 여부 (false 기본값)

    // ==========================
    // 팩토리 메서드 & 유틸
    // ==========================
    public static OrderHistory createRequested(User user, LocalDateTime now) {
        return OrderHistory.builder()
                .user(user)
                .orderStatus(OrderStatus.REQUESTED)
                .orderCreatedAt(now)
                .orderPrice(0L)
                .subtotal(0L)
                .discountCoupon(0L)
                .discountPoints(0L)
                .shippingFee(0L)
                .benefitsReverted(false)
                .orderCode(java.util.UUID.randomUUID().toString())
                .build();
    }

    @PrePersist
    public void init() {
        if (this.orderCode == null) {
            this.orderCode = java.util.UUID.randomUUID().toString();
        }
        if (this.benefitsReverted == null) {
            this.benefitsReverted = false;
        }
    }

    public void changeOrderPrice(long subtotal, long discountCoupon, long discountPoints, long shippingFee) {
        if (subtotal < 0) throw new IllegalArgumentException("금액은 음수일 수 없습니다.");

        this.subtotal = subtotal;
        this.discountCoupon = discountCoupon;
        this.discountPoints = discountPoints;
        this.shippingFee = shippingFee;

        this.orderPrice = subtotal - discountCoupon - discountPoints + shippingFee;
        if (this.orderPrice < 0) this.orderPrice = 0L;
    }

    // ===== 상태 전이 규칙 고정 =====

    /**
     * 고객 취소: 주문요청에서만 허용
     */
    public void cancelByCustomer() {
        if (orderStatus != OrderStatus.REQUESTED) {
            throw new IllegalStateException("현재 상태에서는 주문을 취소할 수 없습니다. 상태=" + orderStatus);
        }
        this.orderStatus = OrderStatus.CANCELED;
    }

    /**
     * 결제 승인: 주문요청에서만
     */
    public void approve() {
        if (orderStatus != OrderStatus.REQUESTED) {
            throw new IllegalStateException("주문요청 상태에서만 결제승인이 가능합니다.");
        }
        this.orderStatus = OrderStatus.APPROVED;
    }

    /**
     * 주문 거절: 주문요청/결제실패(REJECTED)만 허용 (멱등)
     */
    public void reject() {
        if (!(orderStatus == OrderStatus.REQUESTED || orderStatus == OrderStatus.REJECTED)) {
            throw new IllegalStateException("현재 상태에서는 주문을 거절할 수 없습니다.");
        }
        this.orderStatus = OrderStatus.REJECTED; // idempotent
    }

    /**
     * 배송 시작: 승인됨 다음만
     */
    public void ship() {
        if (orderStatus != OrderStatus.APPROVED) {
            throw new IllegalStateException("결제완료 상태에서만 배송을 시작할 수 있습니다.");
        }
        this.orderStatus = OrderStatus.SHIPPED;
    }

    /**
     * 배송 완료: 배송중 다음만
     */
    public void deliver() {
        if (orderStatus != OrderStatus.SHIPPED) {
            throw new IllegalStateException("배송중 상태에서만 배송완료 처리할 수 있습니다.");
        }
        this.orderStatus = OrderStatus.DELIVERED;
    }

    public boolean isCancelable() {
        return this.orderStatus == OrderStatus.REQUESTED;
    }

    public void attachCoupon(String userCouponId) {
        this.userCouponId = userCouponId;
    }

    public void markBenefitsReverted() {
        this.benefitsReverted = true;
    }

    public boolean isBenefitsReverted() {
        return Boolean.TRUE.equals(this.benefitsReverted);
    }
}