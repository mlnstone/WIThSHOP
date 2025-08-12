package com.example.backend.report.dto;

import com.example.backend.report.entity.Report;
import com.example.backend.review.entity.Review;
import com.example.backend.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequestDto {
    private Long reportedUserId; // 피신고자 ID
    private Long reviewId;       // 신고 대상 리뷰 ID

    public Report toEntity(User reporter, User reported, Review review) {
        return Report.builder()
                .reporter(reporter)
                .reported(reported)
                .review(review)
                .isCompleted(false) // 기본 신고 생성 시 미처리 상태
                .build();
    }
}