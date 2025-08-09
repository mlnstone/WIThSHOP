package com.example.backend.point.controller;

import com.example.backend.point.dto.PointAdjustRequest;
import com.example.backend.point.dto.PointBalanceResponse;
import com.example.backend.point.dto.PointChangeRequest;
import com.example.backend.point.service.PointService;
import com.example.backend.pointTransaction.dto.PointTransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관리자-적립금", description = "회원 적립금 조회/내역")
@RestController
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class PointAdminController {

    private final PointService pointService;

    @Operation(summary = "특정 유저 적립금 잔액 조회")
    @GetMapping("/admin/points/{userId}")
    public ResponseEntity<PointBalanceResponse> getUserBalance(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(pointService.getUserBalance(userId));
    }

    @Operation(summary = "특정 유저 적립금 변동 내역 조회")
    @GetMapping("/admin/points/{userId}/transactions")
    public ResponseEntity<Page<PointTransactionResponse>> getUserTransactions(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(pointService.getUserTransactions(userId, pageable));
    }

    @Operation(summary = "특정 유저 포인트 증/차감 (±)")
    @PostMapping("/admin/points/{userId}/adjust")
    public ResponseEntity<PointBalanceResponse> adjustUserPoint(
            @PathVariable Long userId,
            @RequestBody @Valid PointAdjustRequest request
    ) {
        return ResponseEntity.ok(
                pointService.adjustPoint(userId, request.getAmount(), request.getDescription())
        );
    }

    @Operation(summary = "적립금 잔액 설정")
    @PostMapping("/admin/points/{userId}/change")
    public ResponseEntity<PointBalanceResponse> changeUserPoint(
            @PathVariable Long userId,
            @RequestBody @Valid PointChangeRequest request
    ) {
        return ResponseEntity.ok(
                pointService.changePoint(userId, request.getAmount(), request.getDescription())
        );
    }
}