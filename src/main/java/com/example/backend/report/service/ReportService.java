package com.example.backend.report.service;

import com.example.backend.report.dto.ReportAdminResDto;
import com.example.backend.report.dto.ReportRequestDto;
import com.example.backend.report.dto.ReportResponseDto;
import com.example.backend.report.entity.Report;
import com.example.backend.report.repository.ReportRepository;
import com.example.backend.review.entity.Review;
import com.example.backend.review.repository.ReviewRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReportRepository reportRepository;

    public ReportResponseDto getReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));
        return ReportResponseDto.from(report);
    }

    // 특정 유저가 '신고당한' 내역 조회
    public Page<ReportResponseDto> getUserReportedList(Long userId, Pageable pageable) {
        return reportRepository.findByReported_UserId(userId, pageable)
                .map(ReportResponseDto::from);
    }

    public Page<ReportResponseDto> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable).map(ReportResponseDto::from);
    }

    @Transactional
    public ReportResponseDto createReport(String reporterEmail, ReportRequestDto dto) {
        User reporter = userRepository.findByUserEmail(reporterEmail)
                .orElseThrow(() -> new IllegalArgumentException("신고자 정보를 찾을 수 없습니다."));

        User reported = userRepository.findById(dto.getReportedUserId())
                .orElseThrow(() -> new IllegalArgumentException("피신고자 정보를 찾을 수 없습니다."));

        if (reporter.getUserId().equals(reported.getUserId())) {
            throw new IllegalArgumentException("자기 자신을 신고할 수 없습니다.");
        }

        Review review = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰 정보를 찾을 수 없습니다."));

        Report report = dto.toEntity(reporter, reported, review);
        reportRepository.save(report);

        return ReportResponseDto.from(report);
    }

    @Transactional
    public ReportResponseDto processReport(Long reportId, ReportAdminResDto dto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        report.markCompleted(dto.isCompleted()); // <= Report 엔티티에 메서드 필요

        return ReportResponseDto.from(report);
    }
}