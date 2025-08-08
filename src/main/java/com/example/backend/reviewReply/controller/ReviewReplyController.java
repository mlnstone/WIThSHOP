package com.example.backend.reviewReply.controller;

import com.example.backend.reviewReply.dto.ReviewReplyRequestDto;
import com.example.backend.reviewReply.dto.ReviewReplyResponseDto;
import com.example.backend.reviewReply.service.ReviewReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰_답글", description = "리뷰_답글(관리자용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reviews")
public class ReviewReplyController {

    private final ReviewReplyService reviewReplyService;

    @Operation(summary = "리뷰 답글 조회")
    @GetMapping("/{reviewId}/reply")
    public ReviewReplyResponseDto getReviewReply(@PathVariable Long reviewId) {
        return reviewReplyService.getReviewReplyByReviewId(reviewId);
    }

    @Operation(summary = "리뷰 답글 등록")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{reviewId}/reply")
    public ReviewReplyResponseDto createReviewReply(@PathVariable Long reviewId,
                                                    @RequestBody ReviewReplyRequestDto requestDto) {
        return reviewReplyService.createReviewReply(reviewId, requestDto);
    }

    @Operation(summary = "리뷰 답글 수정")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{reviewId}/reply")
    public ReviewReplyResponseDto updateReviewReply(@PathVariable Long reviewId,
                                                    @RequestBody ReviewReplyRequestDto requestDto) {
        return reviewReplyService.updateReviewReply(reviewId, requestDto);
    }

    @Operation(summary = "리뷰 답글 삭제")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{reviewId}/reply")
    public void deleteReviewReply(@PathVariable Long reviewId) {
        reviewReplyService.deleteReviewReply(reviewId);
    }
}