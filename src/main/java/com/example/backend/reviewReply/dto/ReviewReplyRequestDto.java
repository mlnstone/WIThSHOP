package com.example.backend.reviewReply.dto;

import com.example.backend.review.entity.Review;
import com.example.backend.reviewReply.entity.ReviewReply;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewReplyRequestDto {

    private String reviewReplyContent;

    public ReviewReply toEntity(Review review) {
        return ReviewReply.builder()
                .review(review)
                .reviewReplyContent(reviewReplyContent)
                .createdAt(LocalDateTime.now())
                .build();
    }
}