package com.example.backend.reviewReply.dto;

import com.example.backend.reviewReply.entity.ReviewReply;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewReplyResponseDto {

    private Long reviewReplyId;
    private String reviewReplyContent;
    private LocalDateTime createdAt;

    public static ReviewReplyResponseDto from(ReviewReply reviewReply) {
        return ReviewReplyResponseDto.builder()
                .reviewReplyId(reviewReply.getReviewReplyId())
                .reviewReplyContent(reviewReply.getReviewReplyContent())
                .createdAt(reviewReply.getCreatedAt())
                .build();
    }
}