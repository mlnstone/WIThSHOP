package com.example.backend.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    // 만료 시간(밀리초)
    private static final long ACCESS_TOKEN_EXP = 1000L * 60L * 30;          // 30분
    private static final long REFRESH_TOKEN_EXP = 1000L * 60L * 60L * 24L * 7L; // 7일

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 로그인/OAuth2 성공 시 Access + Refresh 동시 발급
     */
    public JwtToken generateToken(Authentication authentication) {
        String auths = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();

        Date accessExp = new Date(now + ACCESS_TOKEN_EXP);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())     // email
                .claim("auth", auths)                      // ROLE_*
                .setExpiration(accessExp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Date refreshExp = new Date(now + REFRESH_TOKEN_EXP);
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())     // ★ subject 넣어둠 (email)
                .setExpiration(refreshExp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 리프레시용: Access 토큰만 새로 발급
     */
    public String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
        long now = System.currentTimeMillis();
        Date accessExp = new Date(now + ACCESS_TOKEN_EXP);

        String auths = authorities == null ? "" :
                authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(username)
                .claim("auth", auths)
                .setExpiration(accessExp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getAccessTokenExpirationTime() {
        return ACCESS_TOKEN_EXP;
    }

    /**
     * Authorization 헤더 혹은 토큰 문자열에서 Bearer 제거
     */
    public String resolveToken(String headerOrToken) {
        if (headerOrToken == null) return null;
        String v = headerOrToken.trim();
        if (v.startsWith("Bearer ")) return v.substring(7);
        return v;
    }

    /**
     * 토큰 → subject(email)
     */
    public String getUserName(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰 유효성 검증 (만료/서명/형식 체크)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    /**
     * Access 토큰 → Authentication
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        Object authObj = claims.get("auth");
        if (authObj == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(authObj.toString().split(","))
                        .filter(s -> !s.isBlank())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 만료된 토큰도 클레임(subject) 파싱 가능하게
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료여도 subject 읽자
        }
    }
}