package com.example.backend.orderHistoryDetail.entity;

import com.example.backend.menu.entity.Menu;
import com.example.backend.orderHistory.entity.OrderHistory;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "order_history_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderHistoryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderHistory orderHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long quantity;

    public static OrderHistoryDetail create(OrderHistory order, Menu menu, long price, long qty) {
        return OrderHistoryDetail.builder()
                .orderHistory(order)
                .menu(menu)
                .price(price)
                .quantity(qty)
                .build();
    }
}