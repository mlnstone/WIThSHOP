package com.example.backend.orderHistory.dto;


public interface MenuBestItemView {
    Long getMenuId();

    String getMenuName();

    String getImage();

    Long getSalePrice();

    Long getTotalQty();

    Long getTotalRevenue();
}