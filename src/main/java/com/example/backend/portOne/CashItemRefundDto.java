package com.example.backend.portOne;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CashItemRefundDto {
    private String impUid;
    private String reason;
    private String orderCode;
}