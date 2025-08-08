package com.example.backend.reviewReply.service;

import com.example.backend.review.entity.Review;
import com.example.backend.review.repository.ReviewRepository;
import com.example.backend.reviewReply.dto.ReviewReplyRequestDto;
import com.example.backend.reviewReply.dto.ReviewReplyResponseDto;
import com.example.backend.reviewReply.entity.ReviewReply;
import com.example.backend.reviewReply.repository.ReviewReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewReplyService {

    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;

    @Transactional(readOnly = true)
    public ReviewReplyResponseDto getReviewReplyByReviewId(Long reviewId) {
        return ReviewReplyResponseDto.from(findReviewReplyByReviewId(reviewId));
    }

    @Transactional
    public ReviewReplyResponseDto createReviewReply(Long reviewId, ReviewReplyRequestDto requestDto) {
        if (reviewReplyRepository.existsByReview_ReviewId(reviewId)) {
            throw new IllegalStateException("이미 해당 리뷰에 대한 답글이 존재합니다.");
        }

        Review review = findReviewById(reviewId);
        ReviewReply reviewReply = requestDto.toEntity(review);
        reviewReplyRepository.save(reviewReply);
        return ReviewReplyResponseDto.from(reviewReply);
    }

    @Transactional
    public ReviewReplyResponseDto updateReviewReply(Long reviewId, ReviewReplyRequestDto requestDto) {
        ReviewReply reviewReply = findReviewReplyByReviewId(reviewId);
        reviewReply.updateContent(requestDto.getReviewReplyContent());
        return ReviewReplyResponseDto.from(reviewReply);
    }

    @Transactional
    public void deleteReviewReply(Long reviewId) {
        ReviewReply reviewReply = findReviewReplyByReviewId(reviewId);
        reviewReplyRepository.delete(reviewReply);
    }
    
    private Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));
    }

    private ReviewReply findReviewReplyByReviewId(Long reviewId) {
        return reviewReplyRepository.findByReview_ReviewId(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰의 답글이 존재하지 않습니다."));
    }
}