package com.example.backend.menu.controller;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.orderHistory.dto.MenuSalesSummaryView;
import com.example.backend.orderHistory.service.OrderReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menus")
public class MenuRankingController {

    private final OrderReportService orderReportService;

    @Operation(summary = "베스트 상품(유저 공개) – 수량 내림차순, 0초과만",
            description = "status 없으면 APPROVED/SHIPPED/DELIVERED만 집계")
    @GetMapping("/best")
    public Page<MenuSalesSummaryView> bestMenus(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @ParameterObject Pageable pageable
    ) {
        return orderReportService.getBestMenusForUsers(status, from, to, pageable);
    }
}