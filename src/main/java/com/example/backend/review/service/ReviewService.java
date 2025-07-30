package com.example.backend.review.service;

import com.example.backend.menu.repository.MenuRepository;
import com.example.backend.review.dto.ReviewAllResponseDto;
import com.example.backend.review.entity.Review;
import com.example.backend.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewAllResponseDto getReview(
            @PathVariable Long reviewId
    ) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰는 존재하지 않습니다."));

        return ReviewAllResponseDto.from(review);
    }

    public Page<ReviewAllResponseDto> getReviewsByMenu(
            Pageable pageable, Long menuId
    ) {
        menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));
        Page<Review> reviews = reviewRepository.findAllByMenu_MenuId(pageable, menuId);
        return reviews.map(ReviewAllResponseDto::from);
    }
}
