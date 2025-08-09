package com.example.backend.point.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointChangeRequest { // 절대값으로 현재 적립금 설정

    @NotNull(message = "포인트 잔액은 필수입니다.")
    @Min(value = 0, message = "포인트 잔액은 0 이상이어야 합니다.")
    private Long amount;

    private String description; // 설명
}