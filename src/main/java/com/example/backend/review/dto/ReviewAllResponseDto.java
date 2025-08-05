package com.example.backend.review.dto;

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

    /**
     * public static ReviewAllResponseDto from(Review review) {
     * return new ReviewAllResponseDto(
     * review.getReviewId(),
     * review.getReviewTitle(),
     * review.getReviewImage(),
     * review.getReviewContent(),
     * review.getRating(),
     * review.getCreatedAt(),
     * maskName(review.getUser().getUserName()),
     * review.getMenu().getMenuId(),
     * review.getMenu().getMenuName()
     * );
     * }
     **/
    public ReviewAllResponseDto withMaskedUserName() {
        return new ReviewAllResponseDto(
                this.reviewId,
                this.reviewTitle,
                this.reviewImage,
                this.reviewContent,
                this.rating,
                this.createdAt,
                mask(this.userName),
                this.menuId,
                this.menuName
        );
    }

    // 마스킹 (김민석 -> 김**)
    private String mask(String name) {
        if (name == null || name.isBlank()) return "";
        return name.charAt(0) + "**";
    }
}
