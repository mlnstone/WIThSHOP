package com.example.backend.point.dto;

import com.example.backend.point.entity.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointBalanceResponse {
    private Long balance;

    public static PointBalanceResponse from(Point point) {
        return new PointBalanceResponse(point.getBalance());
    }
}