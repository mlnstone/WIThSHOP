package com.example.backend.auth.controller;

import com.example.backend.auth.SecurityUtil;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final UserRepository userRepository;

    @GetMapping("/api")
    public Map<String, String> me() {
        String email = SecurityUtil.getCurrentUsername(); // 로그인한 사용자의 이메일
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Map<String, String> res = new HashMap<>();
        res.put("name", user.getUserName());                // ← 이름 반환
        res.put("role", user.getUserType().name());         // ADMIN / CUSTOMER
        return res;
    }
}