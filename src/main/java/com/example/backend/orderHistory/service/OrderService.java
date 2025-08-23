package com.example.backend.orderHistory.service;

import com.example.backend.common.enums.MenuStatus;
import com.example.backend.menu.entity.Menu;
import com.example.backend.menu.repository.MenuRepository;
import com.example.backend.orderHistory.dto.OrderCreateRequest;
import com.example.backend.orderHistory.dto.OrderItemResponse;
import com.example.backend.orderHistory.dto.OrderResponse;
import com.example.backend.orderHistory.entity.OrderHistory;
import com.example.backend.orderHistory.repository.OrderHistoryRepository;
import com.example.backend.orderHistoryDetail.entity.OrderHistoryDetail;
import com.example.backend.orderHistoryDetail.repository.OrderHistoryDetailRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderHistoryDetailRepository orderHistoryDetailRepository;

    @Transactional
    public OrderResponse createOrder(Principal principal, OrderCreateRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("주문 항목이 비어 있습니다.");
        }

        User user = getLoginUser(principal);

        long total = 0L;
        List<OrderItemResponse> itemResponses = new ArrayList<>();

        // 주문 헤더 생성 (REQUESTED)
        OrderHistory order = OrderHistory.createRequested(user, LocalDateTime.now());
        orderHistoryRepository.save(order);

        for (OrderCreateRequest.Item it : req.getItems()) {
            if (it.getQuantity() == null || it.getQuantity() <= 0) {
                throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
            }

            Menu menu = menuRepository.findById(it.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + it.getMenuId()));

            if (menu.getStatus() != MenuStatus.ACTIVE) {
                throw new IllegalArgumentException("판매중이 아닌 상품: " + menu.getMenuName());
            }

            // 재고 차감(엔티티 메서드)
            menu.decreaseStock(it.getQuantity());

            long price = menu.getSalePrice();
            long lineTotal = price * it.getQuantity();
            total += lineTotal;

            // 상세 저장 (DTO -> 엔티티)
            orderHistoryDetailRepository.save(it.toEntity(order, menu, price));

            itemResponses.add(new OrderItemResponse(
                    menu.getMenuId(), menu.getMenuName(), price, it.getQuantity(), lineTotal
            ));
        }

        order.changeOrderPrice(total);

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .orderPrice(order.getOrderPrice())
                .orderCreatedAt(order.getOrderCreatedAt())
                .orderStatus(order.getOrderStatus())
                .items(itemResponses)
                .build();
    }

    @Transactional
    public OrderResponse cancelMyOrder(Principal principal, Long orderId) {
        OrderHistory order = getOwnedOrder(principal, orderId);
        order.cancel();

        List<OrderHistoryDetail> details = orderHistoryDetailRepository.findByOrderHistory(order);
        for (OrderHistoryDetail d : details) {
            d.getMenu().increaseStock(d.getQuantity());
        }
        
        return toOrderResponse(order); // 트랜잭션 내 엔티티 기준 즉시 매핑
    }

    public Page<OrderResponse> getMyOrders(Principal principal, Pageable pageable) {
        User user = getLoginUser(principal);

        return orderHistoryRepository.findByUser(user, pageable)
                .map(this::toOrderResponse);
    }


    // 메서드
    private User getLoginUser(Principal principal) {
        return userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }

    private OrderHistory getOwnedOrder(Principal principal, Long orderId) {
        User user = getLoginUser(principal);
        OrderHistory order = orderHistoryRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));
        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("본인 주문이 아닙니다.");
        }
        return order;
    }

    private OrderResponse toOrderResponse(OrderHistory order) {
        List<OrderHistoryDetail> details = orderHistoryDetailRepository.findByOrderHistory(order);
        List<OrderItemResponse> items = details.stream()
                .map(d -> new OrderItemResponse(
                        d.getMenu().getMenuId(),
                        d.getMenu().getMenuName(),
                        d.getPrice(),
                        d.getQuantity(),
                        d.getPrice() * d.getQuantity()
                ))
                .toList();

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .orderPrice(order.getOrderPrice())
                .orderCreatedAt(order.getOrderCreatedAt())
                .orderStatus(order.getOrderStatus())
                .items(items)
                .build();
    }

    public OrderResponse getMyOrderDetailByCode(Principal principal, String orderCode) {
        User user = getLoginUser(principal);
        OrderHistory order = orderHistoryRepository
                .findByOrderCodeAndUser(orderCode, user)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));
        return toOrderResponse(order);
    }
}