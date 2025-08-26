package com.example.backend.portOne;

import com.example.backend.common.enums.MenuStatus;
import com.example.backend.coupon.service.CouponService;
import com.example.backend.menu.entity.Menu;
import com.example.backend.menu.repository.MenuRepository;
import com.example.backend.point.service.PointService;
import com.example.backend.shipping.service.ShippingFeeConfigService;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortOneService {

    private static final String PREPARE_KEY_PREFIX = "pay:prepare:";
    private static final Duration PREPARE_TTL = Duration.ofMinutes(10);

    private final IamportClient iamportClient;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final CashItemRepository cashItemRepository;
    private final MenuRepository menuRepository;

    // ✅ 혜택/배송비 서비스 주입
    private final CouponService couponService;
    private final PointService pointService;
    private final ShippingFeeConfigService shippingFeeConfigService;

    private final ObjectMapper om = new ObjectMapper();

    /**
     * (기존) 단일 수량/단가 예제용 사전검증 – 유지
     */
    @Transactional(readOnly = true)
    public CashItemPrepareResDto preparePayment(String loginId, CashItemPrepareReqDto dto) {
        findByUser(loginId);

        int quantity = dto.getQuantity();
        if (quantity <= 0) throw new IllegalArgumentException("유효하지 않은 수량입니다");

        int totalAmount = Math.multiplyExact(quantity, 100);
        String merchantUid = UUID.randomUUID().toString();

        try {
            iamportClient.postPrepare(new PrepareData(merchantUid, BigDecimal.valueOf(totalAmount)));
            redisTemplate.opsForValue().set(PREPARE_KEY_PREFIX + merchantUid, String.valueOf(totalAmount), PREPARE_TTL);

            return CashItemPrepareResDto.builder()
                    .merchant_uid(merchantUid)
                    .amount(totalAmount)
                    .quantity(quantity)
                    .build();

        } catch (IOException | IamportResponseException e) {
            log.error("포트원 사전검증 등록 실패 merchantUid={}, amount={}", merchantUid, totalAmount, e);
            throw new RuntimeException("포트원 사전검증 등록 실패", e);
        }
    }

    /**
     * (신규) 장바구니 + 쿠폰 + 포인트 + 배송비 사전검증
     * - 스냅샷을 Redis에 저장
     */
    @Transactional(readOnly = true)
    public PrepareByItemsResponse prepareByItemsWithBenefits(String loginId, PrepareByItemsRequest req) {
        User user = findByUser(loginId);
        Long userId = user.getUserId();

        // 1) 상품 합계
        long subtotal = 0L;
        for (var item : req.items()) {
            long qty = Math.max(1L, item.quantity());
            Menu menu = menuRepository.findById(item.menuId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + item.menuId()));
            if (menu.getStatus() != MenuStatus.ACTIVE) {
                throw new IllegalArgumentException("판매중 아님: " + menu.getMenuName());
            }
            long line = Math.multiplyExact((long) menu.getSalePrice(), qty);
            subtotal = Math.addExact(subtotal, line);
        }

        // 2) 쿠폰 할인 (선택)
        long discountCoupon = 0L;
        String userCouponId = req.userCouponId();
        if (userCouponId != null && !userCouponId.isBlank()) {
            var preview = couponService.previewByUserId(userId, userCouponId, subtotal);
            discountCoupon = preview.getDiscountApplied();
        }

        // 3) 포인트 사용 (가용 잔액/논리 상한)
        long wantUse = req.usePoints() == null ? 0L : Math.max(0, req.usePoints());
        long balance = pointService.getUserBalance(userId).getBalance();
        long maxUsable = Math.min(balance, Math.max(0, subtotal - discountCoupon));
        long discountPoints = Math.min(wantUse, maxUsable);

        // 4) 배송비 (DB 설정값 사용)
        long shippingFee = shippingFeeConfigService.currentAmount();

        // 5) 최종 결제금액
        long finalAmount = Math.max(0, subtotal - discountCoupon - discountPoints + shippingFee);

        // 6) PortOne 사전등록 + 스냅샷 저장
        String merchantUid = UUID.randomUUID().toString();
        try {
            iamportClient.postPrepare(new PrepareData(merchantUid, BigDecimal.valueOf(finalAmount)));

            CheckoutSnapshot snap = new CheckoutSnapshot(
                    userId, subtotal, discountCoupon, discountPoints, shippingFee, finalAmount, userCouponId
            );
            String key = PREPARE_KEY_PREFIX + merchantUid;
            redisTemplate.opsForValue().set(key, om.writeValueAsString(snap), PREPARE_TTL);

            return new PrepareByItemsResponse(merchantUid, finalAmount, subtotal, discountCoupon, discountPoints, shippingFee);

        } catch (IOException | IamportResponseException e) {
            log.error("사전검증 등록 실패 merchantUid={}, amount={}", merchantUid, finalAmount, e);
            throw new RuntimeException("포트원 사전검증 등록 실패", e);
        }
    }

    /**
     * 사후검증: 금액 일치 + 쿠폰 사용 확정 + 포인트 차감 + 영수증 저장 + 스냅샷 삭제
     */
    @Transactional
    public void afterSuccessPayment(String loginId, CashItemVerifyRequest dto) {
        User user = findByUser(loginId);

        try {
            // A) 포트원 결제 조회
            IamportResponse<Payment> resp = iamportClient.paymentByImpUid(dto.getImp_uid());
            Payment payment = resp.getResponse();
            if (payment == null) throw new IllegalArgumentException("결제 정보를 찾을 수 없습니다");

            // B) 상태/주문번호 확인
            String status = String.valueOf(payment.getStatus());
            if (!"paid".equalsIgnoreCase(status)) throw new IllegalArgumentException("결제가 완료되지 않았습니다");
            if (!dto.getMerchant_uid().equals(payment.getMerchantUid()))
                throw new IllegalArgumentException("주문번호 불일치 - 위변조 의심");

            // C) 스냅샷 로드
            String key = PREPARE_KEY_PREFIX + dto.getMerchant_uid();
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) throw new IllegalArgumentException("사전검증 정보가 만료되었거나 존재하지 않습니다");
            CheckoutSnapshot snap = om.readValue(json, CheckoutSnapshot.class);

            if (!snap.getUserId().equals(user.getUserId()))
                throw new IllegalArgumentException("주문자 불일치");

            // D) 금액 일치 확인
            long paidAmount = payment.getAmount().longValueExact();
            if (paidAmount != snap.getFinalAmount())
                throw new IllegalArgumentException("결제 금액 불일치 - 위변조 의심");

            // E) 멱등성
            if (cashItemRepository.existsByImpUid(dto.getImp_uid())) {
                return; // 이미 처리됨
            }

            // F) 혜택 확정 (쿠폰 → 포인트)
            if (snap.getUserCouponId() != null && !snap.getUserCouponId().isBlank() && snap.getDiscountCoupon() > 0) {
                var used = couponService.useByUserId(
                        user.getUserId(), snap.getUserCouponId(), snap.getSubtotal()
                );
                if (!used.getDiscountApplied().equals(snap.getDiscountCoupon())) {
                    throw new IllegalStateException("쿠폰 할인액이 사전계산과 다릅니다.");
                }
            }

            if (snap.getDiscountPoints() > 0) {
                pointService.adjustPointByUserId(
                        user.getUserId(),
                        -snap.getDiscountPoints(),
                        "결제 차감: " + dto.getMerchant_uid()
                );
            }

            // G) 결제 영수증 저장
            CashItem cashItem = CashItem.builder()
                    .impUid(dto.getImp_uid())
                    .merchantUid(dto.getMerchant_uid())
                    .user(user)
                    .amount(paidAmount)
                    .status(status)
                    .build();
            cashItemRepository.save(cashItem);

            // H) 스냅샷 삭제
            redisTemplate.delete(key);

        } catch (IamportResponseException | IOException e) {
            log.error("결제 검증 중 오류 imp_uid={}, merchant_uid={}", dto.getImp_uid(), dto.getMerchant_uid(), e);
            throw new RuntimeException("결제 검증 중 오류 발생", e);
        }
    }

    // (참고) 단순 합계 버전도 유지하려면 남겨둘 수 있음
    @Transactional(readOnly = true)
    public PrepareResponse prepareByItems(String loginId, PrepareRequest req) {
        findByUser(loginId);

        long amount = 0L;
        for (PrepareRequest.Item item : req.items()) {
            Long qty = item.quantity();
            if (qty == null || qty <= 0) throw new IllegalArgumentException("수량은 1 이상");

            Menu menu = menuRepository.findById(item.menuId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + item.menuId()));

            if (menu.getStatus() != MenuStatus.ACTIVE)
                throw new IllegalArgumentException("판매중 아님: " + menu.getMenuName());

            amount += menu.getSalePrice() * qty;
        }

        String merchantUid = UUID.randomUUID().toString();
        try {
            iamportClient.postPrepare(new PrepareData(merchantUid, BigDecimal.valueOf(amount)));
            redisTemplate.opsForValue().set(PREPARE_KEY_PREFIX + merchantUid, String.valueOf(amount), PREPARE_TTL);
            return new PrepareResponse(merchantUid, amount);
        } catch (IOException | IamportResponseException e) {
            throw new RuntimeException("포트원 사전검증 등록 실패", e);
        }
    }

//    @Transactional
//    public CashItemRefundResDto refundPayment(String loginId, CashItemRefundDto dto) {
//        User user = findByUser(loginId);
//
//        try {
//            // 1) 포트원 환불 API 호출
//            CancelData cancelData = new CancelData(dto.getImpUid(), true); // 전액 환불
//            cancelData.setReason(dto.getReason());
//
//            IamportResponse<Payment> resp = iamportClient.cancelPaymentByImpUid(cancelData);
//            Payment payment = resp.getResponse();
//
//            if (payment == null || !"cancelled".equalsIgnoreCase(payment.getStatus())) {
//                throw new IllegalArgumentException("결제 환불 실패");
//            }
//
//            // 2) DB 상태 업데이트
//            CashItem cashItem = cashItemRepository.findByImpUid(dto.getImpUid())
//                    .orElseThrow(() -> new IllegalArgumentException("없는 결제건입니다"));
//
//            cashItem.changePaymentStatus("cancelled"); // 엔티티 메서드에서 status 변경하도록
//
//            // 3) 환불 응답 반환
//            return CashItemRefundResDto.builder()
//                    .impUid(payment.getImpUid())
//                    .status(payment.getStatus())
//                    .cancelledAmount(payment.getCancelAmount().longValue())
//                    .reason(dto.getReason())
//                    .build();
//
//        } catch (IamportResponseException | IOException e) {
//            log.error("환불 처리 실패 imp_uid={}", dto.getImpUid(), e);
//            throw new RuntimeException("결제 환불 중 오류 발생", e);
//        }
//    }

    private User findByUser(String loginId) {
        return userRepository.findByUserEmail(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }
}