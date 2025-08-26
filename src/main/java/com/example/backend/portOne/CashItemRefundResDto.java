package com.example.backend.portOne;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CashItemRefundResDto {
    private final String impUid;       // 환불된 결제건 imp_uid
    private final String status;       // "cancelled"
    private final long cancelledAmount;// 환불된 금액
    private final String reason;       // 환불 사유
}