package com.example.backend.auth.service;

import com.example.backend.auth.JwtToken;
import com.example.backend.auth.JwtTokenProvider;
import com.example.backend.auth.dto.SignUpRequestDto;
import com.example.backend.auth.dto.UserManagementDto;
import com.example.backend.common.enums.Role;
import com.example.backend.common.enums.UserProvider;
import com.example.backend.exception.DuplicateEmailException;
import com.example.backend.point.entity.Point;
import com.example.backend.point.entity.PointSignupConfig;
import com.example.backend.point.repository.PointRepository;
import com.example.backend.point.repository.PointSignupConfigRepository;
import com.example.backend.redis.TokenRedis;
import com.example.backend.redis.TokenRedisRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PointRepository pointRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PointSignupConfigRepository pointSignupConfigRepository;
    private final TokenRedisRepository tokenRedisRepository;
    private final AuthenticationManager authenticationManager;

    public JwtToken login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            JwtToken token = jwtTokenProvider.generateToken(authentication);

            // Redis: email을 키로 access/refresh 저장
            tokenRedisRepository.save(
                    new TokenRedis(
                            email,
                            token.getAccessToken(),
                            token.getRefreshToken()
                    )
            );

            return token;

        } catch (BadCredentialsException e) {
            throw new RuntimeException("아이디 또는 비밀번호가 잘못되었습니다.");
        } catch (AuthenticationException e) {
            throw new RuntimeException("로그인에 실패했습니다.");
        }
    }

    /**
     * ★ Refresh 토큰으로 Access만 재발급
     */
    public JwtToken refresh(String authorizationHeaderOrToken) {
        // 1) "Bearer ..." 제거
        String refreshToken = jwtTokenProvider.resolveToken(authorizationHeaderOrToken);

        // 2) 유효성 검증
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 3) subject(email) 가져오기
        String email = jwtTokenProvider.getUserName(refreshToken);

        // 4) Redis에 저장된 refresh와 일치하는지 검증
        TokenRedis saved = tokenRedisRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("리프레시 토큰이 존재하지 않습니다."));
        if (!refreshToken.equals(saved.getRefreshToken())) {
            throw new RuntimeException("리프레시 토큰이 일치하지 않습니다.");
        }

        // 5) 사용자/권한 조회
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Collection<? extends GrantedAuthority> authorities =
                (user.getUserType() != null)
                        ? List.of(new SimpleGrantedAuthority(user.getUserType().name()))
                        : Collections.emptyList();  // ← 타입 명확

// 6) Access 새로 발급
        String newAccess = jwtTokenProvider.generateAccessToken(email, authorities);

        // 7) Redis에 access 갱신(동일 키(email))
        tokenRedisRepository.save(new TokenRedis(email, newAccess, refreshToken));

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(newAccess)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public UserManagementDto signup(SignUpRequestDto request) {
        if (userRepository.existsByUserEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = request.toEntity(
                encodedPassword,
                Role.CUSTOMER,
                UserProvider.LOCAL
        );

        User saved = userRepository.save(user);

        long signupBonus = pointSignupConfigRepository.findById(1L)
                .map(PointSignupConfig::getAmount)
                .orElse(0L);

        pointRepository.findByUser(saved).orElseGet(() ->
                pointRepository.save(Point.builder()
                        .user(saved)
                        .balance(signupBonus)
                        .build())
        );

        return UserManagementDto.from(saved);
    }
}