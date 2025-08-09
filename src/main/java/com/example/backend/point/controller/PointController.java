package com.example.backend.point.controller;

import com.example.backend.point.dto.PointBalanceResponse;
import com.example.backend.point.service.PointService;
import com.example.backend.pointTransaction.dto.PointTransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "적립금", description = "회원 적립금 조회/내역")
@RestController
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class PointController {

    private final PointService pointService;

    @Operation(summary = "내 적립금 잔액 조회")
    @GetMapping("/points")
    public ResponseEntity<PointBalanceResponse> getMyBalance(Principal principal) {
        return ResponseEntity.ok(pointService.getMyBalance(principal));
    }

    @Operation(summary = "내 적립금 변동 내역 조회")
    @GetMapping("/points/transactions")
    public ResponseEntity<Page<PointTransactionResponse>> getMyTransactions(
            Principal principal,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(pointService.getMyTransactions(principal, pageable));
    }
}