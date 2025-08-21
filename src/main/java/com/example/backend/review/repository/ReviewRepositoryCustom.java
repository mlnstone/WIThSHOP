package com.example.backend.review.repository;

import com.example.backend.review.dto.ReviewPublicResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    ReviewPublicResponseDto findReviewById(Long reviewId);

    Page<ReviewPublicResponseDto> findAllByMenu_MenuId(Pageable pageable, Long menuId);
}
