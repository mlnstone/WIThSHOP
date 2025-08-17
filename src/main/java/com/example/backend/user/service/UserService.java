package com.example.backend.user.service;

import com.example.backend.auth.dto.UserProfileDto;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// com.example.backend.user.service.UserService

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserProfileDto getMyProfile(String email) {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자 없음"));
        return UserProfileDto.from(user);
    }

    @Transactional
    public void changePassword(String email, String currentPw, String newPw) {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자 없음"));

        // 1) 현재 비밀번호 일치 확인
        if (!passwordEncoder.matches(currentPw, user.getUserPwd())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 2) 새 비밀번호가 기존 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(newPw, user.getUserPwd())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }
        user.updatePassword(passwordEncoder.encode(newPw));
    }
}