package com.example.backend.report.dto;

import com.example.backend.report.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDto {
    private Long reportId;
    private Long reporterId;
    private String reporterName;
    private Long reportedId;
    private String reportedName;
    private Long reviewId;
    private boolean isCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReportResponseDto from(Report report) {
        return ReportResponseDto.builder()
                .reportId(report.getReportId())
                .reporterId(report.getReporter().getUserId())
                .reporterName(report.getReporter().getUserName())
                .reportedId(report.getReported().getUserId())
                .reportedName(report.getReported().getUserName())
                .reviewId(report.getReview().getReviewId())
                .isCompleted(report.isCompleted())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}