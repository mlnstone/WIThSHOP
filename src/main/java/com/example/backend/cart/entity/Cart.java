package com.example.backend.cart.entity;

import com.example.backend.menu.entity.Menu;
import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart",
        uniqueConstraints = @UniqueConstraint(name = "uk_cart_user_menu",
                columnNames = {"user_id", "menu_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private Long quantity;

    public void changeQuantity(Long q) {
        this.quantity = q;
    }

    public void addQuantity(Long delta) {
        this.quantity += delta;
    }
}
