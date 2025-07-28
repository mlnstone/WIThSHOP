package com.example.backend.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetails implements UserDetails, OAuth2User {

    private final User user;

    private Map<String, Object> attributes;

    // OAuth2 로그인용 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // OAuth2User 메서드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 권한 (Spring Security 의 "ROLE_")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getUserType().name())
        );
    }

    // 로그인 비밀번호
    @Override
    public String getPassword() {
        return user.getUserPwd();
    }

    // 로그인 ID
    @Override
    public String getUsername() {
        return user.getUserEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 만료 안 됨
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠김 없음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 안 됨
    }

    @Override
    public boolean isEnabled() {
        // 삭제되지 않은 유저만 활성화
        return user.getUserDeletedAt() == null;
    }

    // OAuth2User 고유 ID (필요 시 userId 등으로 변경 가능)
    @Override
    public String getName() {
        return ""; // 또는 user.getUserId().toString()
    }
}