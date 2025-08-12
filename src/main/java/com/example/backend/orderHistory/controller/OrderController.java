package com.example.backend.orderHistory.controller;

import com.example.backend.orderHistory.dto.OrderCreateRequest;
import com.example.backend.orderHistory.dto.OrderResponse;
import com.example.backend.orderHistory.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "주문")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성(메뉴ID/수량 목록으로 즉시 주문)")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            Principal principal,
            @RequestBody OrderCreateRequest request
    ) {
        return ResponseEntity.ok(orderService.createOrder(principal, request));
    }

    @Operation(summary = "내 주문 목록")
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> myOrders(
            Principal principal,
            @ParameterObject
            @PageableDefault(size = 10, sort = "orderId") Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getMyOrders(principal, pageable));
    }

    @Operation(summary = "내 주문 상세 조회")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> myOrderDetail(
            Principal principal,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getMyOrderDetail(principal, orderId));
    }

    @Operation(summary = "주문 취소")
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            Principal principal,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.cancelMyOrder(principal, orderId));
    }
}