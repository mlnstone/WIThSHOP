package com.example.backend.auth.controller;

import com.example.backend.auth.JwtToken;
import com.example.backend.auth.dto.LoginRequestDto;
import com.example.backend.auth.dto.SignUpRequestDto;
import com.example.backend.auth.dto.UserManagementDto;
import com.example.backend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}