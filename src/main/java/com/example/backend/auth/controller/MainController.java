package com.example.backend.auth.controller;

import com.example.backend.auth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/api")
    public Map<String, String> mainPage() {
        String username = SecurityUtil.getCurrentUsername();
        String role = SecurityUtil.getCurrentUserRole();

        Map<String, String> result = new HashMap<>();
        result.put("username", username != null ? username : "비로그인");
        result.put("role", role != null ? role : "권한 없음");
        return result;
    }
}