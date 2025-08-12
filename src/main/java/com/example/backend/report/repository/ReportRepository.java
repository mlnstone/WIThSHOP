package com.example.backend.report.repository;

import com.example.backend.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByReported_UserId(Long userId, Pageable pageable);
}