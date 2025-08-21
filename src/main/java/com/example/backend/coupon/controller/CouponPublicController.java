package com.example.backend.coupon.controller;

import com.example.backend.coupon.dto.CouponApplyResponse;
import com.example.backend.coupon.service.CouponService;
import com.example.backend.userCoupon.dto.UserCouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "쿠폰(회원)")
@RestController
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/coupons")
public class CouponPublicController {

    private final CouponService couponService;

    @Operation(summary = "내 쿠폰 목록")
    @GetMapping
    public ResponseEntity<List<UserCouponResponse>> myCoupons(Principal principal) {
        var list = couponService.getMyCoupons(principal).stream()
                .map(UserCouponResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "쿠폰 적용 시뮬레이션 (사용 전 미리 할인금액 계산)")
    @GetMapping("/{userCouponId}/preview")
    public ResponseEntity<CouponApplyResponse> preview(
            Principal principal,
            @PathVariable String userCouponId,
            @RequestParam Long orderAmount
    ) {
        return ResponseEntity.ok(couponService.preview(principal, userCouponId, orderAmount));
    }

    @Operation(summary = "쿠폰 사용 확정(한 번만)")
    @PostMapping("/{userCouponId}/use")
    public ResponseEntity<CouponApplyResponse> use(
            Principal principal,
            @PathVariable String userCouponId,
            @RequestParam Long orderAmount
    ) {
        return ResponseEntity.ok(couponService.use(principal, userCouponId, orderAmount));
    }
}