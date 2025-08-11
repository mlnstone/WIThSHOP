package com.example.backend.coupon.controller;


import com.example.backend.coupon.dto.CouponCreateRequest;
import com.example.backend.coupon.dto.CouponResponse;
import com.example.backend.coupon.dto.CouponUpdateRequest;
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

    @Operation(summary = "특정 유저의 쿠폰 목록 조회(ADMIN)")
    @GetMapping("/users/{userId}/coupons")
    public ResponseEntity<List<UserCouponResponse>> findUserCoupons(@PathVariable Long userId) {
        List<UserCouponResponse> list = couponService.getUserCoupons(userId).stream()
                .map(UserCouponResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

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

    @Operation(summary = "쿠폰 수정")
    @PutMapping("/{couponId}")
    public ResponseEntity<CouponResponse> update(
            @PathVariable Long couponId,
            @RequestBody @Valid CouponUpdateRequest req
    ) {
        return ResponseEntity.ok(couponService.update(couponId, req));
    }

    @Operation(summary = "쿠폰 삭제")
    @DeleteMapping("/{couponId}")
    public ResponseEntity<Void> delete(@PathVariable Long couponId) {
        couponService.delete(couponId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "특정 유저의 보유 쿠폰 회수(ADMIN)")
    @DeleteMapping("/users/{userId}/coupons/{userCouponId}")
    public ResponseEntity<Void> revokeUserCoupon(
            @PathVariable Long userId,
            @PathVariable String userCouponId
    ) {
        couponService.revokeFromUser(userId, userCouponId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모든 유저에게 쿠폰 일괄 발급(ADMIN)")
    @PostMapping("/{couponId}/issue-all")
    public ResponseEntity<Long> issueToAll(@PathVariable Long couponId) {
        long issued = couponService.issueToAllUsers(couponId);
        return ResponseEntity.ok(issued); // 발급된 개수 리턴
    }
}