package com.example.backend.pointTransaction.dto;

import com.example.backend.pointTransaction.entity.PointTransaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PointTransactionResponse {
    private Long pointTransactionId;
    private Long amount;
    private String description;
    private LocalDateTime createdAt;

    public static PointTransactionResponse from(PointTransaction tx) {
        return new PointTransactionResponse(
                tx.getPointTransactionId(),
                tx.getAmount(),
                tx.getDescription(),
                tx.getCreatedAt()
        );
    }
}