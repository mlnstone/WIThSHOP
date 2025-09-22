// src/main/java/com/example/backend/orderHistoryDetail/repository/OrderHistoryDetailRepository.java
package com.example.backend.orderHistoryDetail.repository;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.orderHistory.dto.MenuSalesByUserDto;
import com.example.backend.orderHistory.dto.MenuSalesSummaryView;
import com.example.backend.orderHistory.entity.OrderHistory;
import com.example.backend.orderHistoryDetail.entity.OrderHistoryDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface OrderHistoryDetailRepository extends JpaRepository<OrderHistoryDetail, Long> {
    List<OrderHistoryDetail> findByOrderHistory(OrderHistory orderHistory);

    @Query("""
                select new com.example.backend.orderHistory.dto.MenuSalesByUserDto(
                    u.userId, u.userName, sum(d.quantity), sum(d.price * d.quantity),
                    count(distinct oh.orderId)
                )
                from OrderHistoryDetail d
                join d.orderHistory oh
                join oh.user u
                where d.menu.menuId = :menuId
                  and (:status is null or oh.orderStatus = :status)
                  and (:from is null or oh.orderCreatedAt >= :from)
                  and (:to   is null or oh.orderCreatedAt <  :to)
                group by u.userId, u.userName
                order by sum(d.quantity) desc
            """)
    Page<MenuSalesByUserDto> findMenuSalesByUser(
            Long menuId,
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    @Query("""
                select (count(ohd) > 0)
                from OrderHistoryDetail ohd
                where ohd.orderHistory.user.userId = :userId
                  and ohd.menu.menuId = :menuId
                  and ohd.orderHistory.orderCode = :orderCode
                  and ohd.orderHistory.orderStatus in :statuses
            """)
    boolean existsPurchasedInOrder(Long userId, Long menuId, String orderCode, Set<OrderStatus> statuses);

    // -------- 관리자/공용 집계(원가/판매가 포함) --------
    @Query(
            value = """
                        SELECT
                          m.menu_id                 AS menuId,
                          m.menu_name               AS menuName,
                          m.cost_price              AS costPrice,   -- ★
                          m.sale_price              AS salePrice,   -- ★
                          SUM(d.quantity)           AS totalQty,
                          SUM(d.price * d.quantity) AS totalAmount
                        FROM order_history_detail d
                        JOIN order_history o ON o.order_id = d.order_id
                        JOIN menu m          ON m.menu_id  = d.menu_id
                        WHERE
                          (
                            (:status IS NULL AND o.order_status IN ('APPROVED','SHIPPED','DELIVERED'))
                            OR (:status IS NOT NULL AND o.order_status = :status)
                          )
                          AND (:from IS NULL OR o.order_created_at >= :from)
                          AND (:to   IS NULL OR o.order_created_at <  :to)
                        GROUP BY m.menu_id, m.menu_name, m.cost_price, m.sale_price
                        HAVING SUM(d.quantity) > 0
                        ORDER BY SUM(d.quantity) DESC
                    """,
            countQuery = """
                        SELECT COUNT(1) FROM (
                          SELECT m.menu_id
                          FROM order_history_detail d
                          JOIN order_history o ON o.order_id = d.order_id
                          JOIN menu m          ON m.menu_id  = d.menu_id
                          WHERE
                            (
                              (:status IS NULL AND o.order_status IN ('APPROVED','SHIPPED','DELIVERED'))
                              OR (:status IS NOT NULL AND o.order_status = :status)
                            )
                            AND (:from IS NULL OR o.order_created_at >= :from)
                            AND (:to   IS NULL OR o.order_created_at <  :to)
                          GROUP BY m.menu_id
                          HAVING SUM(d.quantity) > 0
                        ) t
                    """,
            nativeQuery = true
    )
    Page<MenuSalesSummaryView> findMenuSalesSummary(
            @Param("status") OrderStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    // src/main/java/com/example/backend/orderHistoryDetail/repository/OrderHistoryDetailRepository.java
    @Query(
            value = """
                    SELECT
                      m.menu_id                                  AS menuId,
                      m.menu_name                                AS menuName,
                      SUM(d.quantity)                            AS totalQty,
                      m.cost_price                               AS costPrice,
                      m.sale_price                               AS salePrice,
                      SUM(d.price * d.quantity)                  AS totalRevenue,
                      SUM((m.sale_price - m.cost_price) * d.quantity) AS totalProfit
                    FROM order_history_detail d
                    JOIN order_history o ON o.order_id = d.order_id
                    JOIN menu m          ON m.menu_id = d.menu_id
                    WHERE
                      (
                        (:status IS NULL AND o.order_status IN ('APPROVED','SHIPPED','DELIVERED'))
                        OR (:status IS NOT NULL AND o.order_status = :status)
                      )
                      AND (:from IS NULL OR o.order_created_at >= :from)
                      AND (:to   IS NULL OR o.order_created_at <  :to)
                    GROUP BY m.menu_id, m.menu_name, m.cost_price, m.sale_price
                    HAVING SUM(d.quantity) > 0
                    ORDER BY SUM(d.quantity) DESC
                    """,
            countQuery = """
                    SELECT COUNT(1) FROM (
                      SELECT m.menu_id
                      FROM order_history_detail d
                      JOIN order_history o ON o.order_id = d.order_id
                      JOIN menu m          ON m.menu_id = d.menu_id
                      WHERE
                        (
                          (:status IS NULL AND o.order_status IN ('APPROVED','SHIPPED','DELIVERED'))
                          OR (:status IS NOT NULL AND o.order_status = :status)
                        )
                        AND (:from IS NULL OR o.order_created_at >= :from)
                        AND (:to   IS NULL OR o.order_created_at <  :to)
                      GROUP BY m.menu_id
                      HAVING SUM(d.quantity) > 0
                    ) t
                    """,
            nativeQuery = true
    )
    Page<MenuSalesSummaryView> findBestMenusForUsers(
            @Param("status") OrderStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
}