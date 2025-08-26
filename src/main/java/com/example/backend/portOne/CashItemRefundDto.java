package com.example.backend.portOne;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CashItemRefundDto {
    private String impUid;   // 환불할 결제건의 imp_uid
    private String reason;   // 환불 사유
}