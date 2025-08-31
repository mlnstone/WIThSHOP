package com.example.backend.auth.controller;

import com.example.backend.auth.JwtToken;
import com.example.backend.auth.dto.EmailCheckResponse;
import com.example.backend.auth.dto.LoginRequestDto;
import com.example.backend.auth.dto.SignUpRequestDto;
import com.example.backend.auth.dto.UserManagementDto;
import com.example.backend.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@Valid @RequestBody LoginRequestDto request) {
        JwtToken jwtToken = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserManagementDto> signup(@Valid @RequestBody SignUpRequestDto request) {
        UserManagementDto response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtToken> refresh(@RequestHeader(name = "Authorization", required = false) String authorization) {
        JwtToken token = authService.refresh(authorization);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(
            @RequestParam @NotBlank @Email String email
    ) {
        boolean available = authService.isEmailAvailable(email);
        return ResponseEntity.ok(new EmailCheckResponse(available));
    }
}