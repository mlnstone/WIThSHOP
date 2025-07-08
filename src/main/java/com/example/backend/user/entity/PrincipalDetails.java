package com.example.backend.user.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class PrincipalDetails implements UserDetails {

    private final User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    // 권한 반환 (예: ROLE_ADMIN, ROLE_CUSTOMER 등)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_" 접두어는 Spring Security의 규칙
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getUserType().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getUserPwd(); // 비밀번호 필드명 확인 필요
    }

    @Override
    public String getUsername() {
        return user.getUserEmail(); // 로그인에 사용하는 ID , 이름 아님, 구현하는 겨
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 안 됨
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 아님
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 안 됨
    }

    @Override
    public boolean isEnabled() {
        // 탈퇴하지 않은 유저만 활성화
        return user.getUserDeletedAt() == null;
    }
}