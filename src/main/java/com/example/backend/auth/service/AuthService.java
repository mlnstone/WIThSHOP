package com.example.backend.auth.service;

import com.example.backend.auth.JwtToken;
import com.example.backend.auth.JwtTokenProvider;
import com.example.backend.auth.dto.SignUpRequestDto;
import com.example.backend.auth.dto.UserManagementDto;
import com.example.backend.common.enums.Role;
import com.example.backend.common.enums.UserProvider;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public JwtToken login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            return jwtTokenProvider.generateToken(authentication);
        } catch (BadCredentialsException e) {
            // 아이디 또는 비밀번호가 틀린 경우
            throw new RuntimeException("아이디 또는 비밀번호가 잘못되었습니다.");
        } catch (AuthenticationException e) {
            // 기타 인증 실패
            throw new RuntimeException("로그인에 실패했습니다.");
        }
    }

    public UserManagementDto signup(SignUpRequestDto request) {
        if (userRepository.existsByUserEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = request.toEntity(
                encodedPassword,
                Role.CUSTOMER,
                UserProvider.LOCAL
        );

        User saved = userRepository.save(user);
        return UserManagementDto.from(saved);
    }
}
