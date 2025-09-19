package com.example.backend.config;

import com.example.backend.auth.JwtAuthenticationFilter;
import com.example.backend.auth.JwtToken;
import com.example.backend.auth.JwtTokenProvider;
import com.example.backend.auth.service.CustomOAuth2UserService;
import com.example.backend.exception.OAuth2AuthenticationFailureHandler;
import com.example.backend.redis.TokenRedis;
import com.example.backend.redis.TokenRedisRepository;
import com.example.backend.user.entity.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRedisRepository tokenRedisRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private static final String FRONT_CALLBACK =
            System.getenv().getOrDefault("FRONT_CALLBACK", "http://localhost:3000/oauth2/callback");

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST API 기본 설정
                .httpBasic().disable()
                .csrf().disable()
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 인가 설정 (context-path=/api 인 경우에도 아래 경로들은 그대로 써도 됨: 보안 매칭은 상대 경로)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 공개 엔드포인트
                        .requestMatchers(
                                "/members/login",
                                "/members/sign-up",
                                "/members/refresh",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/configuration/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/oauth2/authorization/**",
                                "/actuator/**",   // ALB 헬스체크
                                "/"               // 루트
                        ).permitAll()
                        // 나머지는 인증 필요 (원하면 permitAll 로 바꿔도 됨)
                        .anyRequest().permitAll()
                )

                // OAuth2 로그인 (성공 시 프론트 콜백으로 토큰 전달)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        .successHandler((request, response, authentication) -> {
                            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

                            // JWT 발급
                            JwtToken token = jwtTokenProvider.generateToken(authentication);

                            // RefreshToken Redis 저장
                            tokenRedisRepository.save(new TokenRedis(principal.getUsername(), token.getRefreshToken()));

                            // 프론트 콜백 URL로 리다이렉트 (토큰 전달)
                            String redirectUrl = FRONT_CALLBACK
                                    + "?accessToken=" + URLEncoder.encode(token.getAccessToken(), StandardCharsets.UTF_8)
                                    + "&refreshToken=" + URLEncoder.encode(token.getRefreshToken(), StandardCharsets.UTF_8);

                            response.sendRedirect(redirectUrl);
                        })
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                );

        // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 전에 실행
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 설정
     * - 자격증명(쿠키/인증헤더) 사용하려면 Origin을 * 로 둘 수 없음 → 정확한 도메인 나열
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of( // www 사용 시
                "http://localhost:3000"
        ));
        // 와일드카드가 필요하면 대신:
        // cfg.setAllowedOriginPatterns(List.of("https://*.wit-h.shop", "http://localhost:*"));

        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);
        return request -> cfg;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}