package com.example.backend.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        String uri = httpReq.getRequestURI();

        // 1) 리프레시 엔드포인트는 인증 시도 스킵 (refresh 토큰에는 auth 클레임이 없음)
        if ("/members/refresh".equals(uri)) {
            chain.doFilter(request, response);
            return;
        }

        // 2) Authorization 헤더에서 토큰 추출
        String token = resolveToken(httpReq);

        // 3) access 토큰으로 보이는 것만 유효성 검사 + 컨텍스트 세팅 시도
        if (token != null && jwtTokenProvider.validateToken(token)) {
            try {
                // auth 클레임 없는 토큰(refresh)이면 아래에서 RuntimeException 발생 → catch에서 무시
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ignore) {
                // 권한 클레임이 없거나 기타 문제면 컨텍스트 세팅하지 않고 그냥 다음 필터로
            }
        }

        chain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) { // 공백 포함
            return bearerToken.substring(7);
        }
        return null;
    }
}