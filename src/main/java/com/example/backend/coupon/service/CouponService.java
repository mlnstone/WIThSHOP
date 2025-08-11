package com.example.backend.coupon.service;

import com.example.backend.common.enums.CouponStatus;
import com.example.backend.common.enums.DiscountType;
import com.example.backend.coupon.dto.CouponApplyResponse;
import com.example.backend.coupon.dto.CouponCreateRequest;
import com.example.backend.coupon.dto.CouponResponse;
import com.example.backend.coupon.entity.Coupon;
import com.example.backend.coupon.repository.CouponRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.userCoupon.entity.UserCoupon;
import com.example.backend.userCoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    // ADMIN
    @Transactional
    public CouponResponse create(CouponCreateRequest req) {

        if (req.getDiscountType() == DiscountType.PERCENT) {
            Long d = req.getDiscount();
            if (d == null || d < 1 || d > 100) {
                throw new IllegalArgumentException("퍼센트 할인은 1~100 사이여야 합니다.");
            }
        }

        // 코드 중복 방지
        couponRepository.findByCode(req.getCode()).ifPresent(c -> {
            throw new IllegalArgumentException("이미 존재하는 쿠폰 코드입니다.");
        });

        Coupon saved = couponRepository.save(
                Coupon.builder()
                        .couponName(req.getCouponName())
                        .code(req.getCode())
                        .discountType(req.getDiscountType())
                        .discount(req.getDiscount())
                        .limitQuantity(req.getLimitQuantity())
                        .minAmount(req.getMinAmount())
                        .createdAt(LocalDateTime.now())
                        .limitAt(req.getLimitAt())
                        .build()
        );
        return CouponResponse.from(saved);
    }

    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }

    @Transactional
    public UserCoupon issueToUser(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰"));

        if (coupon.getLimitAt() != null && coupon.getLimitAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료된 쿠폰");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));

        // 중복 보유 방지
        if (userCouponRepository.countByCoupon_CouponIdAndUser(couponId, user) > 0) {
            throw new IllegalStateException("이미 보유한 쿠폰입니다.");
        }

        // 발급 한도 동시성 안전 체크 & 차감
        if (coupon.getLimitQuantity() != null) {
            int updated = couponRepository.decreaseQuantityIfAvailable(couponId);
            if (updated == 0) {
                throw new IllegalArgumentException("발급 한도 소진");
            }
        }

        // 발급
        UserCoupon uc = UserCoupon.builder()
                .userCouponId(UUID.randomUUID().toString())
                .user(user)
                .coupon(coupon)
                .isUsed(CouponStatus.UNUSED)
                .build();

        return userCouponRepository.save(uc);
    }

    // USER
    public List<UserCoupon> getMyCoupons(Principal principal) {
        Long userId = getCurrentUserId(principal);
        return userCouponRepository.findWithCouponByUserId(userId);

    }

    public CouponApplyResponse preview(Principal principal, String userCouponId, Long orderAmount) {
        Long userId = getCurrentUserId(principal);
        UserCoupon uc = userCouponRepository
                .findWithCouponByIdAndUserId(userCouponId, userId)
                .orElseThrow(() -> new IllegalArgumentException("내 쿠폰이 아님"));

        long discountApplied = calcDiscount(uc.getCoupon(), orderAmount);
        long pay = Math.max(0, orderAmount - discountApplied);
        return new CouponApplyResponse(orderAmount, discountApplied, pay, "preview");
    }

    @Transactional
    public CouponApplyResponse use(Principal principal, String userCouponId, Long orderAmount) {
        Long userId = getCurrentUserId(principal);
        UserCoupon uc = userCouponRepository
                .findWithCouponByIdAndUserId(userCouponId, userId)
                .orElseThrow(() -> new IllegalArgumentException("내 쿠폰이 아님"));

        if (uc.getIsUsed() == CouponStatus.USED) {
            throw new IllegalStateException("이미 사용한 쿠폰");
        }
        Coupon coupon = uc.getCoupon();

        if (coupon.getLimitAt() != null && coupon.getLimitAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료된 쿠폰");
        }
        if (coupon.getMinAmount() != null && orderAmount < coupon.getMinAmount()) {
            throw new IllegalArgumentException("최소 주문금액 미달");
        }

        long discountApplied = calcDiscount(coupon, orderAmount);
        long pay = Math.max(0, orderAmount - discountApplied);

        // 사용 처리
        uc = UserCoupon.builder()
                .userCouponId(uc.getUserCouponId())
                .user(uc.getUser())
                .coupon(uc.getCoupon())
                .isUsed(CouponStatus.USED)
                .build();
        userCouponRepository.save(uc);

        return new CouponApplyResponse(orderAmount, discountApplied, pay, "used");
    }

    // 메서드
    private Long getCurrentUserId(Principal principal) {
        return userRepository.findIdByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
    }

    private long calcDiscount(Coupon coupon, long orderAmount) {
        long d = coupon.getDiscount();
        if (coupon.getDiscountType() == DiscountType.PERCENT) {
            long pct = Math.max(0, Math.min(d, 100));
            return (orderAmount * pct) / 100;
        } else {
            return Math.min(orderAmount, Math.max(0, d));
        }
    }

    public List<UserCoupon> getUserCoupons(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));
        return userCouponRepository.findWithCouponByUserId(userId);
    }
}