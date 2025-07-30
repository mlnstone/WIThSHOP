package com.example.backend.review.dto;

import com.example.backend.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewAdminResponseDto {

    private Long reviewId;
    private String reviewTitle;
    private String reviewImage;
    private String reviewContent;
    private Double rating;
    private LocalDateTime createdAt;

    private Long userId;
    private String userName;
    private String userEmail;

    private Long menuId;
    private String menuName;

    public static ReviewAdminResponseDto from(Review review) {
        return new ReviewAdminResponseDto(
                review.getReviewId(),
                review.getReviewTitle(),
                review.getReviewImage(),
                review.getReviewContent(),
                review.getRating(),
                review.getCreatedAt(),
                review.getUser().getUserId(),
                review.getUser().getUserName(),
                review.getUser().getUserEmail(),
                review.getMenu().getMenuId(),
                review.getMenu().getMenuName()
        );
    }
}