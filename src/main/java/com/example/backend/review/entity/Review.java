package com.example.backend.review.entity;

import com.example.backend.common.base.EntityDate;
import com.example.backend.menu.entity.Menu;
import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String reviewTitle;

    private String reviewImage;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reviewContent;

    @Column(nullable = false)
    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 36)
    private String orderCode;

    public void update(String reviewTitle, String reviewContent, String reviewImage, Double rating) {
        if (reviewTitle != null) this.reviewTitle = reviewTitle;
        if (reviewContent != null) this.reviewContent = reviewContent;
        if (reviewImage != null) this.reviewImage = reviewImage;
        if (rating != null) this.rating = rating;
    }
}