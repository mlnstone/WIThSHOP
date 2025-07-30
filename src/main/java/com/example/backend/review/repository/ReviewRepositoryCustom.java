package com.example.backend.review.repository;

import com.example.backend.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    Page<Review> findAllByMenu_MenuId(Pageable pageable, Long menuId);
}
