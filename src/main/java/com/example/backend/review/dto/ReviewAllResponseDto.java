package com.example.backend.review.dto;

import com.example.backend.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewAllResponseDto {
    private Long reviewId;
    private String reviewTitle;
    private String reviewImage;
    private String reviewContent;
    private Double rating;
    private LocalDateTime createdAt;

    private String userName;

    private Long menuId;
    private String menuName;

    public static ReviewAllResponseDto from(Review review) {
        return new ReviewAllResponseDto(
                review.getReviewId(),
                review.getReviewTitle(),
                review.getReviewImage(),
                review.getReviewContent(),
                review.getRating(),
                review.getCreatedAt(),
                maskName(review.getUser().getUserName()),
                review.getMenu().getMenuId(),
                review.getMenu().getMenuName()
        );
    }

    // 김민석 -> 김** , 이황 -> 이** (무조건 3글자)로 마스킹
    public static String maskName(String name) {
        if (name == null || name.isBlank()) return "";

        return name.charAt(0) + "**";
    }
}
