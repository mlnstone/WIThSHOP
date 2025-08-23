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
    private Long orderPrice;

    @Column(nullable = false)
    private LocalDateTime orderCreatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static OrderHistory createRequested(User user, LocalDateTime now) {
        return OrderHistory.builder()
                .user(user)
                .orderStatus(OrderStatus.REQUESTED)
                .orderCreatedAt(now)
                .orderPrice(0L)
                .orderCode(java.util.UUID.randomUUID().toString())
                .build();
    }

    @PrePersist
    public void init() {
        if (this.orderCode == null) {
            this.orderCode = java.util.UUID.randomUUID().toString();
        }
    }

    public void changeOrderPrice(long price) {
        if (price < 0) throw new IllegalArgumentException("금액은 음수일 수 없습니다.");
        this.orderPrice = price;
    }

    public void cancel() {
        if (!isCancelable()) {
            throw new IllegalStateException("현재 상태에서는 취소할 수 없습니다. 상태=" + orderStatus);
        }
        this.orderStatus = OrderStatus.CANCELED;
    }

    public void changeStatusByAdmin(OrderStatus status) {
        this.orderStatus = status;
    }

    public boolean isCancelable() {
        return switch (this.orderStatus) {
            case REQUESTED, APPROVED, REJECTED -> true;
            default -> false;
        };
    }
}