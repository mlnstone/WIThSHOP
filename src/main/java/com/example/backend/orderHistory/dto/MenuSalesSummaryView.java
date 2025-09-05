package com.example.backend.orderHistory.dto;

public interface MenuSalesSummaryView {
    Long getMenuId();

    String getMenuName();

    Long getTotalQty();

    Long getTotalAmount();
}