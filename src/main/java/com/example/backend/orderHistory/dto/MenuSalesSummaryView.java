package com.example.backend.orderHistory.dto;

public interface MenuSalesSummaryView {
    Long getMenuId();

    String getMenuName();

    Long getTotalQty();

    Long getCostPrice();     // 원가

    Long getSalePrice();     // 판매가

    Long getTotalRevenue();  // 총매출 = SUM(d.price * d.quantity)

    Long getTotalProfit();   // 순이익 = SUM((m.sale_price - m.cost_price) * d.quantity)
}