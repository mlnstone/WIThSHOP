package com.example.backend.portOne;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CashItemVerifyRequest {
    private String imp_uid;      // 포트원 결제 고유번호
    private String merchant_uid; // 사전검증 주문번호

    public CashItemVerifyRequest(String imp_uid, String merchant_uid) {
        this.imp_uid = imp_uid;
        this.merchant_uid = merchant_uid;
    }
}