package com.example.backend.coupon.controller;


import com.example.backend.coupon.dto.CouponCreateRequest;
import com.example.backend.coupon.dto.CouponResponse;
import com.example.backend.coupon.service.CouponService;
import com.example.backend.userCoupon.dto.UserCouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관리자-쿠폰")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/coupons")
public class CouponAdminController {

    private final CouponService couponService;

    @Operation(summary = "쿠폰 생성")
    @PostMapping
    public ResponseEntity<CouponResponse> create(@RequestBody @Valid CouponCreateRequest req) {
        return ResponseEntity.ok(couponService.create(req));
    }

    @Operation(summary = "쿠폰 목록")
    @GetMapping
    public ResponseEntity<List<CouponResponse>> findAll() {
        return ResponseEntity.ok(
                couponService.findAll().stream().map(CouponResponse::from).toList()
        );
    }


    @Operation(summary = "특정 쿠폰을 특정 유저에게 발급")
    @PostMapping("/{couponId}/issue/{userId}")
    public ResponseEntity<UserCouponResponse> issue(@PathVariable Long couponId, @PathVariable Long userId) {
        return ResponseEntity.ok(UserCouponResponse.from(couponService.issueToUser(couponId, userId)));
    }
}