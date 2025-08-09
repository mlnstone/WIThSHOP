package com.example.backend.point.entity;

import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Long balance = 0L;

    public void add(long amount) {
        this.balance += amount;
    }

    public void subtract(long amount) {
        if (this.balance < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.balance -= amount;
    }

    public void setBalance(long balance) { // change 용
        if (balance < 0) {
            throw new IllegalArgumentException("포인트는 음수가 될 수 없습니다.");
        }
        this.balance = balance;
    }
}
