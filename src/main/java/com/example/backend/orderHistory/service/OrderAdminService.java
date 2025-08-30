package com.example.backend.orderHistory.service;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.menu.entity.Menu;
import com.example.backend.menu.repository.MenuRepository;
import com.example.backend.orderHistory.dto.OrderItemResponse;
import com.example.backend.orderHistory.dto.OrderResponse;
import com.example.backend.orderHistory.entity.OrderHistory;
import com.example.backend.orderHistory.repository.OrderHistoryRepository;
import com.example.backend.orderHistoryDetail.entity.OrderHistoryDetail;
import com.example.backend.orderHistoryDetail.repository.OrderHistoryDetailRepository;
import com.example.backend.portOne.CashItem;
import com.example.backend.portOne.CashItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderAdminService {

    private final MenuRepository menuRepository;
    private CashItemRepository cashItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderHistoryDetailRepository orderHistoryDetailRepository;

    public Page<OrderResponse> list(Long userId, OrderStatus status, Pageable pageable) {
        Page<OrderHistory> page;
        if (userId != null && status != null) {
            page = orderHistoryRepository.findByUser_UserIdAndOrderStatus(userId, status, pageable);
        } else if (userId != null) {
            page = orderHistoryRepository.findByUser_UserId(userId, pageable);
        } else if (status != null) {
            page = orderHistoryRepository.findByOrderStatus(status, pageable);
        } else {
            page = orderHistoryRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    public OrderResponse detail(Long orderId) {
        OrderHistory order = orderHistoryRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));
        return toResponse(order);
    }

    @Transactional
    public OrderResponse changeStatus(Long orderId, OrderStatus status) {
        OrderHistory order = orderHistoryRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));

        order.changeStatusByAdmin(status);
        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancel(Long orderId) {
        OrderHistory order = orderHistoryRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));

        if (order.isCancelable()) {
            List<OrderHistoryDetail> details = orderHistoryDetailRepository.findByOrderHistory(order);
            for (OrderHistoryDetail d : details) {
                Menu m = d.getMenu();
                m.increaseStock(d.getQuantity()); // Menu 엔티티에 추가한 메서드
            }
        }
        order.cancel(); // CANCELED 처리
        return toResponse(order);
    }

    private OrderResponse toResponse(OrderHistory order) {
        // 1) 상세 아이템 조회
        List<OrderHistoryDetail> details = orderHistoryDetailRepository.findByOrderHistory(order);

        List<OrderItemResponse> items = details.stream()
                .map(d -> new OrderItemResponse(
                        d.getMenu().getMenuId(),
                        d.getMenu().getMenuName(),
                        d.getPrice(),
                        d.getQuantity(),
                        d.getPrice() * d.getQuantity()))
                .toList();

        CashItem cashItem = cashItemRepository.findByOrderCode(order.getOrderCode())
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 없습니다"));

        return OrderResponse.from(order, cashItem, items);
    }
}