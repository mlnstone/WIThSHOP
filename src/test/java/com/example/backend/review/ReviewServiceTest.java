package com.example.backend.review;

import com.example.backend.review.repository.ReviewRepository;
import com.example.backend.review.service.ReviewService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReviewServiceTest {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EntityManager em;


    @Test
    @DisplayName("리뷰 단건 조회 QueryDSL")
    void 리뷰_1만번_단건_조회_querydsl() {
        Long reviewId = 1L;

        long start = System.currentTimeMillis();


        reviewService.getReview(reviewId);


        long end = System.currentTimeMillis();
        System.out.println("걸린 시간(ms) = " + (end - start));
    }
}