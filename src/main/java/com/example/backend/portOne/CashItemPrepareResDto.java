package com.example.backend.portOne;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CashItemPrepareResDto {
    private final String merchant_uid; // 주문번호
    private final int amount;          // 총 금액
    private final int quantity;        // 구매 수량
}