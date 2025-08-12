package com.example.backend.orderHistoryDetail.repository;

import com.example.backend.orderHistory.dto.MenuSalesByUserDto;
import com.example.backend.orderHistory.entity.OrderHistory;
import com.example.backend.orderHistoryDetail.entity.OrderHistoryDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderHistoryDetailRepository extends JpaRepository<OrderHistoryDetail, Long> {
    List<OrderHistoryDetail> findByOrderHistory(OrderHistory orderHistory);

    @Query("""
                select new com.example.backend.orderHistory.dto.MenuSalesByUserDto(
                    u.userId,
                    u.userName,
                    sum(d.quantity),
                    sum(d.price * d.quantity),
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
            com.example.backend.common.enums.OrderStatus status, // 필터 옵션
            java.time.LocalDateTime from,                        // 시작일(옵션)
            java.time.LocalDateTime to,                          // 종료일(옵션, 미만)
            Pageable pageable
    );
}