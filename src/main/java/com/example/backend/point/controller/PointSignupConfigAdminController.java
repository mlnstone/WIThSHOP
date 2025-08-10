package com.example.backend.point.controller;

import com.example.backend.point.service.PointSignupConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관리자-적립금")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/signup-point")
@PreAuthorize("hasRole('ADMIN')")
public class PointSignupConfigAdminController {

    private final PointSignupConfigService signupPointConfigService;

    @Operation(summary = "가입 적립금 현재값 조회")
    @GetMapping
    public ResponseEntity<Long> get() {
        return ResponseEntity.ok(signupPointConfigService.currentAmount());
    }

    @Operation(summary = "가입 적립금 설정(숫자만)")
    @PutMapping("/{amount}")
    public ResponseEntity<Long> set(@PathVariable long amount) {
        return ResponseEntity.ok(signupPointConfigService.updateAmount(amount));
    }
}