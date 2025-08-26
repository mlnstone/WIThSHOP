package com.example.backend.portOne;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CashItemPrepareReqDto {
    private int quantity;

    // 생성자나 정적 팩토리로만 만들 수 있게 제한
    public CashItemPrepareReqDto(int quantity) {
        this.quantity = quantity;
    }
}