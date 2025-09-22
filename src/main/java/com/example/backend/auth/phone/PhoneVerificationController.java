package com.example.backend.auth.phone;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/phone")
public class PhoneVerificationController {

    private final PhoneVerificationService service;

    @PostMapping("/request")
    public ResponseEntity<Void> request(@RequestBody @Valid RequestDto req) {
        service.requestCodeForSignup(req.getPhone());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyRes> verify(@RequestBody @Valid VerifyReq req) {
        boolean ok = service.verifyCodeForSignup(req.getPhone(), req.getCode());
        return ResponseEntity.ok(new VerifyRes(ok));
    }

    @Getter
    static class RequestDto {
        @NotBlank
        private String phone;
    }

    @Getter
    static class VerifyReq {
        @NotBlank
        private String phone;
        @NotBlank
        private String code;
    }

    @Getter
    static class VerifyRes {
        private final boolean verified;

        public VerifyRes(boolean v) {
            this.verified = v;
        }
    }
}