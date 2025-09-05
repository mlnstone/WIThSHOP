package com.example.backend.review.controller;

import com.example.backend.review.dto.ReviewPublicResponseDto;
import com.example.backend.review.dto.ReviewUpdateRequestDto;
import com.example.backend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "내 리뷰", description = "내가 작성한 리뷰 조회/수정/삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("/me/reviews")
public class MyReviewController {

    private final ReviewService reviewService;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내 리뷰 목록 조회 (페이징)")
    @GetMapping
    public ResponseEntity<Page<ReviewPublicResponseDto>> getMyReviews(
            Principal principal,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getMyReviews(principal, pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내 리뷰 단건 조회")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewPublicResponseDto> getMyReview(
            Principal principal,
            @PathVariable Long reviewId
    ) {
        return ResponseEntity.ok(reviewService.getMyReview(principal, reviewId));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내 리뷰 수정 (작성 후 3일 이내)")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewPublicResponseDto> updateMyReview(
            Principal principal,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequestDto dto
    ) {
        return ResponseEntity.ok(reviewService.updateMyReview(principal, reviewId, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내 리뷰 삭제 (작성 후 3일 이내)")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteMyReview(
            Principal principal,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteMyReview(principal, reviewId);
        return ResponseEntity.noContent().build();
    }
}