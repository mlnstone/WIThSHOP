package com.example.backend.user.service;

import com.example.backend.auth.dto.UserProfileDto;
import com.example.backend.common.enums.Gender;
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
        int count = 0;
        System.out.println(++count + "==========================================");
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자 없음"));

        boolean alreadyComplete =
                user.getUserName() != null &&
                        user.getBirth() != null &&
                        user.getGender() != null &&
                        user.getPhone() != null;

        if (alreadyComplete) {
            // 예외 대신 현재 프로필 그대로 반환 (멱등)
            return UserProfileDto.from(user);
        }

        // 비어있는 항목만 채우고, 들어온 값이 없으면 기존 값 유지
        String name = (user.getUserName() == null) ? req.getName() : user.getUserName();
        String birth = (user.getBirth() == null) ? req.getBirth() : user.getBirth();
        Gender gender = (user.getGender() == null) ? req.getGender() : user.getGender();
        String phone = (user.getPhone() == null) ? req.getPhone() : user.getPhone();

        user.updateProfile(name, birth, gender, phone);
        return UserProfileDto.from(user);
    }
}