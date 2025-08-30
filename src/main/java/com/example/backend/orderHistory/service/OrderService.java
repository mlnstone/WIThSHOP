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
import com.example.backend.portOne.CashItem;
import com.example.backend.portOne.CashItemRepository;
import com.example.backend.shipping.service.ShippingFeeConfigService;
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
    private final CashItemRepository cashItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ShippingFeeConfigService shippingFeeConfigService;
    private final OrderHistoryDetailRepository orderHistoryDetailRepository;


    @Transactional
    public OrderResponse createOrder(Principal principal, OrderCreateRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("주문 항목이 비어 있습니다.");
        }

        User user = getLoginUser(principal);

        long subtotal = 0L;
        List<OrderItemResponse> itemResponses = new ArrayList<>();

        // 주문 헤더 생성 (REQUESTED)
        OrderHistory order = OrderHistory.createRequested(user, LocalDateTime.now());
        orderHistoryRepository.save(order);

        // 상세/합계
        for (OrderCreateRequest.Item it : req.getItems()) {
            if (it.getQuantity() == null || it.getQuantity() <= 0) {
                throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
            }

            Menu menu = menuRepository.findById(it.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + it.getMenuId()));

            if (menu.getStatus() != MenuStatus.ACTIVE) {
                throw new IllegalArgumentException("판매중이 아닌 상품: " + menu.getMenuName());
            }

            // 재고 차감
            menu.decreaseStock(it.getQuantity());

            long price = menu.getSalePrice();
            long lineTotal = Math.multiplyExact(price, it.getQuantity());
            subtotal = Math.addExact(subtotal, lineTotal);

            orderHistoryDetailRepository.save(it.toEntity(order, menu, price));

            itemResponses.add(new OrderItemResponse(
                    menu.getMenuId(), menu.getMenuName(), price, it.getQuantity(), lineTotal
            ));
        }

        long shippingFee = (req.getShippingFee() != null)
                ? Math.max(0, req.getShippingFee())
                : Math.max(0, shippingFeeConfigService.currentAmount());

        long discountCoupon = Math.max(0, (req.getDiscountCoupon() == null ? 0L : req.getDiscountCoupon()));
        long discountPointsReq = Math.max(0, (req.getDiscountPoints() == null ? 0L : req.getDiscountPoints()));

        long maxUsablePoints = Math.max(0, subtotal - discountCoupon);
        long discountPoints = Math.min(discountPointsReq, maxUsablePoints);

        if (req.getUserCouponId() != null && !req.getUserCouponId().isBlank()) {
            order.attachCoupon(req.getUserCouponId());
        }

        order.changeOrderPrice(subtotal, discountCoupon, discountPoints, shippingFee);

        String mu = req.getMerchantUid();
        if (mu != null && !mu.isBlank()) {
            cashItemRepository.findByMerchantUid(mu)
                    .ifPresent(ci -> ci.attachOrderCode(order.getOrderCode()));
        }

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

        // 재고 롤백
        List<OrderHistoryDetail> details = orderHistoryDetailRepository.findByOrderHistory(order);
        for (OrderHistoryDetail d : details) {
            d.getMenu().increaseStock(d.getQuantity());
        }

        return toOrderResponse(order);
    }

    public Page<OrderResponse> getMyOrders(Principal principal, Pageable pageable) {
        User user = getLoginUser(principal);
        return orderHistoryRepository.findByUser(user, pageable)
                .map(this::toOrderResponse);
    }

    public OrderResponse getMyOrderDetailByCode(Principal principal, String orderCode) {
        User user = getLoginUser(principal);
        OrderHistory order = orderHistoryRepository
                .findByOrderCodeAndUser(orderCode, user)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));
        return toOrderResponse(order);
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
        
        String impUid = cashItemRepository.findByOrderCode(order.getOrderCode())
                .map(CashItem::getImpUid)
                .orElse(null);

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .orderPrice(order.getOrderPrice())
                .orderCreatedAt(order.getOrderCreatedAt())
                .orderStatus(order.getOrderStatus())
                .items(items)
                .impUid(impUid)
                .build();
    }
}