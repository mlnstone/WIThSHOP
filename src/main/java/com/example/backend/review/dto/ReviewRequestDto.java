package com.example.backend.review.dto;

import com.example.backend.menu.entity.Menu;
import com.example.backend.review.entity.Review;
import com.example.backend.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {

    private String reviewTitle;
    private String reviewContent;
    private String reviewImage;
    private Double rating;
    private Long menuId;
    private String orderCode;   // 주문번호 받기


    public Review toEntity(User user, Menu menu) {
        return Review.builder()
                .reviewTitle(reviewTitle)
                .reviewContent(reviewContent)
                .reviewImage(reviewImage)
                .rating(rating)
                .user(user)
                .menu(menu)
                .orderCode(orderCode)
                .build();
    }
}