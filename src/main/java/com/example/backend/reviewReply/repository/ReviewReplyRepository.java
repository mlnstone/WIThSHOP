package com.example.backend.reviewReply.repository;

import com.example.backend.reviewReply.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {
    Optional<ReviewReply> findByReview_ReviewId(Long reviewId);

    boolean existsByReview_ReviewId(Long reviewId);
}
