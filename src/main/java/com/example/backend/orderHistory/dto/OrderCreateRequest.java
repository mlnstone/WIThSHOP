package com.example.backend.orderHistory.dto;

import com.example.backend.menu.entity.Menu;
import com.example.backend.orderHistory.entity.OrderHistory;
import com.example.backend.orderHistoryDetail.entity.OrderHistoryDetail;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreateRequest {
    private List<Item> items;

    @Getter
    public static class Item {
        private Long menuId;
        private Long quantity;

        public OrderHistoryDetail toEntity(OrderHistory order, Menu menu, long price) {
            return OrderHistoryDetail.create(order, menu, price, quantity);
        }
    }
}