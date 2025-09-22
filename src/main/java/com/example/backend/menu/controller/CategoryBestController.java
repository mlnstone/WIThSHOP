package com.example.backend.menu.controller;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.orderHistory.dto.MenuBestItemView;
import com.example.backend.orderHistory.service.OrderReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryBestController {

    private final OrderReportService orderReportService;

    @Operation(summary = "카테고리별 베스트 메뉴 Top-N (기본 3)")
    @GetMapping("/{categoryId}/best")
    public ResponseEntity<List<MenuBestItemView>> bestInCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "3") int limit
    ) {
        var list = orderReportService.getBestByCategory(categoryId, status, from, to, limit);
        // 프론트에서 list.length === 0 이면 "아직 없습니다" 노출
        return ResponseEntity.ok(list);
    }
}