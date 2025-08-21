package com.example.backend.shipping.controller;

import com.example.backend.shipping.service.ShippingFeeConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관리자-배송비")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/shipping-fee")
@PreAuthorize("hasRole('ADMIN')")
public class ShippingFeeConfigAdminController {

    private final ShippingFeeConfigService service;

    @Operation(summary = "배송비 현재값 조회")
    @GetMapping
    public ResponseEntity<Long> get() {
        return ResponseEntity.ok(service.currentAmount());
    }

    @Operation(summary = "배송비 설정 (숫자만)")
    @PutMapping("/{amount}")
    public ResponseEntity<Long> set(@PathVariable long amount) {
        return ResponseEntity.ok(service.updateAmount(amount));
    }
}