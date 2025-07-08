package com.example.backend.auth.controller;

import com.example.backend.auth.SecurityUtil;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequiredArgsConstructor
public class MainController {

    private final UserRepository userRepository;
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public String mainPage() {

        String username = SecurityUtil.getCurrentUsername();
        String role = SecurityUtil.getCurrentUserRole();
        String roleText = role.equals("ROLE_ADMIN") ? "관리자" : "고객";

        return "메인페이지 - 현재 로그인한 사용자: " + username + " / 권한: " + roleText;
    }
}