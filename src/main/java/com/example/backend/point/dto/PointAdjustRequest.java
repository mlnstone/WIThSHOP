package com.example.backend.point.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointAdjustRequest { // 증감

    @NotNull(message = "포인트 증감 금액은 필수입니다.")
    private Long amount;

    private String description; // 설명
}