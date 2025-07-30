package com.example.backend.review.controller;

import com.example.backend.review.dto.ReviewAllResponseDto;
import com.example.backend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "리뷰", description = "리뷰")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 단건 조회")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewAllResponseDto> getReviews(
            @PathVariable Long reviewId
    ) {
        ReviewAllResponseDto reviewAllResponseDto = reviewService.getReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(reviewAllResponseDto);
    }

    @Operation(summary = "메뉴의 전체 리뷰 페이징 조회")
    @GetMapping("/menus/{menuId}/reviews")
    public ResponseEntity<Page<ReviewAllResponseDto>> getReviewsByMenu(
            @PageableDefault Pageable pageable,
            @PathVariable Long menuId

    ) {
        Page<ReviewAllResponseDto> result = reviewService.getReviewsByMenu(pageable, menuId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
