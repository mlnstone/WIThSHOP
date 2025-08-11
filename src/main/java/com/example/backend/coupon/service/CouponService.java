package com.example.backend.coupon.service;

import com.example.backend.common.enums.CouponState;
import com.example.backend.common.enums.CouponStatus;
import com.example.backend.common.enums.DiscountType;
import com.example.backend.coupon.dto.CouponApplyResponse;
import com.example.backend.coupon.dto.CouponCreateRequest;
import com.example.backend.coupon.dto.CouponResponse;
import com.example.backend.coupon.dto.CouponUpdateRequest;
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
                        .state(CouponState.ACTIVE)
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

        // 1. 상태 체크
        if (coupon.getState() != CouponState.ACTIVE) {
            if (coupon.getState() == CouponState.INACTIVE) {
                throw new IllegalArgumentException("현재 발급이 중단된 쿠폰입니다.");
            } else if (coupon.getState() == CouponState.EXPIRED) {
                throw new IllegalArgumentException("만료된 쿠폰입니다.");
            }
        }

        // 2. 만료일 체크 (EXPIRED 상태와 중복 방지)
        if (coupon.getLimitAt() != null && coupon.getLimitAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료된 쿠폰");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));

        // 3. 중복 보유 방지
        if (userCouponRepository.countByCoupon_CouponIdAndUser(couponId, user) > 0) {
            throw new IllegalStateException("이미 보유한 쿠폰입니다.");
        }

        // 4. 발급 한도 체크 & 차감
        if (coupon.getLimitQuantity() != null) {
            int updated = couponRepository.decreaseQuantityIfAvailable(couponId);
            if (updated == 0) {
                throw new IllegalArgumentException("발급 한도 소진");
            }
        }

        // 5. 발급
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
        return userCouponRepository.findActiveWithCouponByUserId(userId, LocalDateTime.now());
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

        Coupon c = uc.getCoupon();
        if (c.getLimitAt() != null && c.getLimitAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료된 쿠폰");
        }
        if (c.getMinAmount() != null && orderAmount < c.getMinAmount()) {
            throw new IllegalArgumentException("최소 주문금액 미달");
        }

        long discountApplied = calcDiscount(c, orderAmount);
        long pay = Math.max(0, orderAmount - discountApplied);

        uc.markUsed();

        return new CouponApplyResponse(orderAmount, discountApplied, pay, "used");
    }

    @Transactional
    public CouponResponse update(Long couponId, CouponUpdateRequest req) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰"));

        if (req.getDiscountType() == DiscountType.PERCENT) {
            Long d = req.getDiscount();
            if (d == null || d < 1 || d > 100) {
                throw new IllegalArgumentException("퍼센트 할인은 1~100 사이여야 합니다.");
            }
        }

        couponRepository.findByCode(req.getCode()).ifPresent(found -> {
            if (!found.getCouponId().equals(couponId)) {
                throw new IllegalArgumentException("이미 존재하는 쿠폰 코드입니다.");
            }
        });

        coupon.update(
                req.getCouponName(),
                req.getCode(),
                req.getDiscountType(),
                req.getDiscount(),
                req.getLimitQuantity(),
                req.getMinAmount(),
                req.getLimitAt(),
                req.getState()
        );

        return CouponResponse.from(coupon);
    }

    @Transactional
    public void delete(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰"));
        couponRepository.delete(coupon);
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

    @Transactional
    public void revokeFromUser(Long userId, String userCouponId) {
        UserCoupon uc = userCouponRepository
                .findWithCouponByIdAndUserId(userCouponId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 쿠폰이 아닙니다."));

        // 이미 사용된 쿠폰은 회수 불가
        if (uc.getIsUsed() == CouponStatus.USED) {
            throw new IllegalStateException("이미 사용된 쿠폰은 삭제할 수 없습니다.");
        }

        // 삭제(회수)
        userCouponRepository.delete(uc);

        // 발급 한도를 운영 중이라면 수량 복구
        Coupon coupon = uc.getCoupon();
        if (coupon.getLimitQuantity() != null) {
            couponRepository.increaseQuantity(coupon.getCouponId());
        }
    }

    @Transactional
    public long issueToAllUsers(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰"));
        if (coupon.getState() != CouponState.ACTIVE) throw new IllegalArgumentException("발급 불가 상태의 쿠폰입니다.");
        if (coupon.getLimitAt() != null && coupon.getLimitAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("만료된 쿠폰입니다.");

        int pageSize = 500;
        long issued = 0;
        for (int page = 0; ; page++) {
            var pageable = org.springframework.data.domain.PageRequest.of(page, pageSize);
            var users = userRepository.findAll(pageable);
            if (users.isEmpty()) break;

            var ids = users.stream().map(User::getUserId).toList();
            var ownedIds = new java.util.HashSet<>(userCouponRepository.findOwnedUserIds(couponId, ids));

            var toIssue = new java.util.ArrayList<UserCoupon>(pageSize);
            for (User u : users) {
                if (ownedIds.contains(u.getUserId())) continue;

                if (coupon.getLimitQuantity() != null) {
                    int ok = couponRepository.decreaseQuantityIfAvailable(couponId);
                    if (ok == 0) return issued; // 한도 소진
                }

                toIssue.add(UserCoupon.builder()
                        .userCouponId(java.util.UUID.randomUUID().toString())
                        .user(u)
                        .coupon(coupon)
                        .isUsed(com.example.backend.common.enums.CouponStatus.UNUSED)
                        .build());
            }
            userCouponRepository.saveAll(toIssue); // 배치 인서트
            issued += toIssue.size();

            if (!users.hasNext()) break;
        }
        return issued;
    }
}