package com.example.backend.review.controller;

import com.example.backend.review.dto.ReviewPublicResponseDto;
import com.example.backend.review.dto.ReviewRequestDto;
import com.example.backend.review.dto.ReviewSummaryDto;
import com.example.backend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Tag(name = "리뷰", description = "리뷰")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 단건 조회")
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewPublicResponseDto> getReviews(
            @PathVariable Long reviewId
    ) {
        ReviewPublicResponseDto reviewPublicResponseDto = reviewService.getReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(reviewPublicResponseDto);
    }

    @Operation(summary = "메뉴의 전체 리뷰 페이징 조회")
    @GetMapping("/menus/{menuId}/reviews")
    public ResponseEntity<Page<ReviewPublicResponseDto>> getReviewsByMenu(
            @PageableDefault Pageable pageable,
            @PathVariable Long menuId

    ) {
        Page<ReviewPublicResponseDto> result = reviewService.getReviewsByMenu(pageable, menuId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "리뷰 작성")
    @PostMapping("/reviews")
    public ResponseEntity<ReviewPublicResponseDto> createReview(
            Principal principal,
            @RequestBody ReviewRequestDto reviewRequestDto
    ) {
        ReviewPublicResponseDto reviewPublicResponseDto = reviewService.createReview(principal, reviewRequestDto);
        return ResponseEntity.ok(reviewPublicResponseDto);
    }

    @Operation(summary = "메뉴의 리뷰 요약(개수/평균)")
    @GetMapping("/menus/{menuId}/reviews/summary")
    public ResponseEntity<ReviewSummaryDto> getReviewSummary(@PathVariable Long menuId) {
        return ResponseEntity.ok(reviewService.getReviewSummary(menuId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/reviews/exists")
    public ResponseEntity<Map<String, Boolean>> existsMyReview(
            Principal principal,
            @RequestParam Long menuId,
            @RequestParam String orderCode
    ) {
        boolean exists = reviewService.hasMyReview(principal, menuId, orderCode);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

}
