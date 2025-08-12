package com.example.backend.orderHistory.controller;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.orderHistory.dto.OrderResponse;
import com.example.backend.orderHistory.service.OrderAdminService;
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

@Tag(name = "관리자-주문")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class OrderAdminController {

    private final OrderAdminService orderAdminService;

    @Operation(summary = "주문 목록(필터 가능: userId, status)")
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) OrderStatus status,
            @ParameterObject @PageableDefault(size = 10, sort = "orderId") Pageable pageable
    ) {
        return ResponseEntity.ok(orderAdminService.list(userId, status, pageable));
    }

    @Operation(summary = "주문 단건 조회")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> detail(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderAdminService.detail(orderId));
    }

    @Operation(summary = "주문 상태 변경")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> changeStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(orderAdminService.changeStatus(orderId, status));
    }

    @Operation(summary = "주문 취소(관리자 강제 취소)")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderAdminService.cancel(orderId));
    }
}