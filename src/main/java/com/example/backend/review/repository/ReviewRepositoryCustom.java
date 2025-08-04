package com.example.backend.review.repository;

import com.example.backend.review.dto.ReviewAllResponseDto;
import com.example.backend.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    ReviewAllResponseDto findReviewById(Long reviewId);

    Page<Review> findAllByMenu_MenuId(Pageable pageable, Long menuId);
}
