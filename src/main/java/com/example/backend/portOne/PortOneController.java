package com.example.backend.portOne;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "포트원")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PortOneController {

    private final PortOneService portOneService;

    // 1. 사전검증
    @PostMapping("/prepare")
    public ResponseEntity<CashItemPrepareResDto> preparePayment(
            Principal principal,   // 로그인한 사용자
            @Valid @RequestBody CashItemPrepareReqDto dto) {

        String loginId = principal.getName(); // 보통 username(email) 반환
        return ResponseEntity.ok(portOneService.preparePayment(loginId, dto));
    }

    // 2. 사후검증
    @PostMapping("/verify")
    public ResponseEntity<Void> afterSuccessPayment(
            Principal principal,
            @Valid @RequestBody CashItemVerifyRequest dto) {

        String loginId = principal.getName();
        portOneService.afterSuccessPayment(loginId, dto);
        return ResponseEntity.noContent().build(); // 204
    }

    @PostMapping("/prepare-by-items")
    public ResponseEntity<PrepareByItemsResponse> prepareByItems(
            Principal principal,
            @Valid @RequestBody PrepareByItemsRequest req
    ) {
        String loginId = principal.getName();
        return ResponseEntity.ok(portOneService.prepareByItemsWithBenefits(loginId, req));
    }
}