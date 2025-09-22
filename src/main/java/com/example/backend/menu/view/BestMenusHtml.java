// src/main/java/com/example/backend/menu/view/BestMenusHtml.java
package com.example.backend.menu.view;

import com.example.backend.orderHistory.dto.MenuSalesSummaryView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BestMenusHtml {

    public static String build(List<MenuSalesSummaryView> items,
                               LocalDateTime from, LocalDateTime to) {
        var df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        var range = (from != null || to != null)
                ? String.format("(%s ~ %s)",
                from != null ? df.format(from) : "-",
                to != null ? df.format(to) : "-")
                : "(전체)";

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"ko\">");
        sb.append("<head>");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        sb.append("<style>");
        sb.append("body { font-family: 'Noto Sans KR', 'Malgun Gothic', sans-serif; }");
        sb.append("h1 { margin-bottom: 8px; font-size: 20px; }");
        sb.append(".desc { color:#555; font-size:12px; margin-bottom: 12px; }");
        sb.append("table { width: 100%; border-collapse: collapse; font-size: 12px; }");
        sb.append("th, td { border: 1px solid #ddd; padding: 6px 8px; }");
        sb.append("th { background: #f5f5f5; text-align: left; }");
        sb.append(".num { text-align: right; }");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");

        sb.append("<h1>베스트 상품 랭킹 ").append(range).append("</h1>");
        sb.append("<div class='desc'>수량 내림차순 (0 초과만 집계)</div>");

        sb.append("<table>");
        sb.append("<thead>");
        sb.append("<tr>");
        sb.append("<th style=\"width:48px;\">#</th>");
        sb.append("<th>상품명</th>");
        sb.append("<th class=\"num\">판매 수량</th>");
        sb.append("<th class=\"num\">원가</th>");
        sb.append("<th class=\"num\">판매가</th>");
        sb.append("<th class=\"num\">총 매출</th>");
        sb.append("<th class=\"num\">순이익</th>");
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");

        int rank = 1;
        for (var it : items) {
            sb.append("<tr>");
            sb.append("<td>").append(rank++).append("</td>");
            sb.append("<td>").append(escape(it.getMenuName())).append("</td>");
            sb.append("<td class=\"num\">").append(fmt(it.getTotalQty())).append("</td>");
            sb.append("<td class=\"num\">").append(fmt(it.getCostPrice())).append("</td>");
            sb.append("<td class=\"num\">").append(fmt(it.getSalePrice())).append("</td>");
            sb.append("<td class=\"num\">").append(fmt(it.getTotalRevenue())).append("</td>");
            sb.append("<td class=\"num\">").append(fmt(it.getTotalProfit())).append("</td>");
            sb.append("</tr>");
        }

        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    private static String fmt(Number n) {
        return (n == null) ? "0" : String.format("%,d", n.longValue());
    }

    private static String escape(String s) {
        return s == null ? "" : s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}