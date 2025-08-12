package com.example.backend.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportAdminResDto {
    private boolean isCompleted; // true면 신고 처리 완료
}