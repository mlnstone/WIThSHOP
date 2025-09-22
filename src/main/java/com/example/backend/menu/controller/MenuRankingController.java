package com.example.backend.menu.controller;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.common.pdf.PdfRenderService;
import com.example.backend.menu.view.BestMenusHtml;
import com.example.backend.orderHistory.dto.MenuSalesSummaryView;
import com.example.backend.orderHistory.service.OrderReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menus")
public class MenuRankingController {
    private final PdfRenderService pdfRenderService;
    private final OrderReportService orderReportService;

    @Operation(summary = "베스트 상품(유저 공개) – 수량 내림차순, 0초과만",
            description = "status 없으면 APPROVED/SHIPPED/DELIVERED만 집계")
    @GetMapping("/best")
    public Page<MenuSalesSummaryView> bestMenus(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @ParameterObject Pageable pageable
    ) {
        return orderReportService.getBestMenusForUsers(status, from, to, pageable);
    }

    @Operation(summary = "베스트 상품 PDF 다운로드")
    @GetMapping("/best.pdf")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<byte[]> bestMenusPdf(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @ParameterObject Pageable pageable
    ) {
        // JSON과 다르게 Page 말고 content만 가져옴
        var page = orderReportService.getBestMenusForUsers(status, from, to, pageable);
        List<MenuSalesSummaryView> items = page.getContent();

        // HTML → PDF 변환
        String html = BestMenusHtml.build(items, from, to);
        byte[] pdfBytes = pdfRenderService.renderHtmlToPdf(html);

        // 응답 헤더 설정
        String filename = "best-menus.pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}