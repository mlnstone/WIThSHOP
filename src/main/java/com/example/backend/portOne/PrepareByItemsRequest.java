package com.example.backend.portOne;

import java.util.List;

public record PrepareByItemsRequest(
        List<Item> items,
        String userCouponId,   // ✅ 내 쿠폰 ID (선택)
        Long usePoints         // 선택 (사용할 적립금)
) {
    public record Item(Long menuId, Long quantity) {
    }
}