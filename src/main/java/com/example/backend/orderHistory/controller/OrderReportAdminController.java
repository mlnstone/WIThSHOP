package com.example.backend.orderHistory.controller;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.orderHistory.dto.MenuSalesByUserDto;
import com.example.backend.orderHistory.service.OrderReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class OrderReportAdminController {

    private final OrderReportService orderReportService;

    @Operation(summary = "특정 메뉴의 사용자별 판매 통계")
    @GetMapping("/menus/{menuId}/stats/users")
    public Page<MenuSalesByUserDto> menuSalesByUser(
            @PathVariable Long menuId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @ParameterObject Pageable pageable
    ) {
        return orderReportService.getMenuSalesByUser(menuId, status, from, to, pageable);
    }
}