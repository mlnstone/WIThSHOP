package com.example.backend.portOne;

public record PrepareByItemsResponse(
        String merchant_uid,
        long amount,          // 최종 결제금액(= subtotal - coupon - points + shipping)
        long subtotal,        // 상품합계
        long discountCoupon,  // 쿠폰할인
        long discountPoints,  // 포인트사용
        long shippingFee      // 배송비
) {
}