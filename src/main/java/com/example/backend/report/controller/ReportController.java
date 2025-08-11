//package com.example.backend.report.controller;
//
//
//import com.example.backend.report.dto.ReportAdminResDto;
//import com.example.backend.report.dto.ReportRequestDto;
//import com.example.backend.report.dto.ReportResponseDto;
//import com.example.backend.report.repository.ReportRepository;
//import com.example.backend.report.service.ReportService;
//import com.example.backend.user.repository.UserRepository;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springdoc.core.annotations.ParameterObject;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.security.Principal;
//
//@Tag(name = "신고")
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/reports")
//public class ReportController {
//
//    private final ReportService reportService;
//    private final UserRepository userRepository;
//    private final ReportRepository reportRepository;
//
//    @Operation(summary = "신고 단건 조회")
//    @GetMapping("/{reportId}")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<ReportResponseDto> getReport(@PathVariable Long reportId) {
//        ReportResponseDto dto = reportService.getReport(reportId);
//        return ResponseEntity.status(HttpStatus.OK).body(dto);
//    }
//
//    @Operation(summary = "유저가 받은 신고 전체 조회")
//    @GetMapping("/user/{userId}")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<Page<ReportResponseDto>> getUserReports(
//            @PathVariable Long userId,
//            @ParameterObject
//            @PageableDefault(size = 10, sort = "reportId") Pageable pageable
//    ) {
//        Page<ReportResponseDto> page = reportService.getUserReportedList(userId, pageable);
//        return ResponseEntity.ok(page);
//    }
//
//    @Operation(summary = "신고 전체 조회")
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping
//    public ResponseEntity<Page<ReportResponseDto>> getAllReports(
//            @ParameterObject
//            @PageableDefault(size = 10, sort = "reportId") Pageable pageable
//    ) {
//        Page<ReportResponseDto> page = reportService.getAllReports(pageable);
//        return ResponseEntity.ok(page);
//    }
//
//    @Operation(summary = "신고 작성", description = "신고 생성")
//    @PostMapping
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<ReportResponseDto> createReport(
//            @RequestBody ReportRequestDto reportDto,
//            Principal principal
//    ) {
//        // principal.getName() = 로그인한 사용자의 이메일
//        Long reporterId = userRepository.findIdByEmail(principal.getName())
//                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
//
//        ReportResponseDto dto = reportService.createReport(reporterId, reportDto);
//        return ResponseEntity.ok(dto);
//    }
//
//    @Operation(summary = "신고 처리(ADMIN)")
//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/{reportId}")
//    public ResponseEntity<ReportResponseDto> updateReport(
//            @PathVariable Long reportId,
//            @RequestBody ReportAdminResDto reportAdminResDto
//    ) {
//        ReportResponseDto dto = reportService.processReport(reportId, reportAdminResDto);
//        return ResponseEntity.ok(dto);
//    }
//}