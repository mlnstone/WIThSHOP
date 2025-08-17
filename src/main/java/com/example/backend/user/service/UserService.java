package com.example.backend.user.service;

import com.example.backend.auth.dto.UserProfileDto;
import com.example.backend.common.enums.UserProvider;
import com.example.backend.user.dto.UpdateProfileRequest;
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

        if (user.getUserProvider() != UserProvider.LOCAL) {
            throw new IllegalStateException("소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
        }

        if (!passwordEncoder.matches(currentPw, user.getUserPwd())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        if (passwordEncoder.matches(newPw, user.getUserPwd())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }
        user.updatePassword(passwordEncoder.encode(newPw));
    }

    @Transactional
    public UserProfileDto updateOauth2Profile(String email, UpdateProfileRequest req) {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자 없음"));
        
        // 이미 모든 프로필 정보가 채워져 있다면 수정 불가
        if (user.getUserName() != null &&
                user.getBirth() != null &&
                user.getGender() != null &&
                user.getPhone() != null) {
            throw new IllegalStateException("프로필은 최초 1회만 설정할 수 있습니다.");
        }

        user.updateProfile(req.getName(), req.getBirth(), req.getGender(), req.getPhone());
        return UserProfileDto.from(user);
    }
}