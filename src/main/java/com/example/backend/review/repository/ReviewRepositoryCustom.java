package com.example.backend.review.repository;

import com.example.backend.review.dto.ReviewAllResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    ReviewAllResponseDto findReviewById(Long reviewId);

    Page<ReviewAllResponseDto> findAllByMenu_MenuId(Pageable pageable, Long menuId);
}
