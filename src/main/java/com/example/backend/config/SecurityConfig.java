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
import org.springframework.web.cors.CorsConfigurationSource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRedisRepository tokenRedisRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;


    // 콜백 url
    private static final String FRONT_CALLBACK = "http://localhost:3000/oauth2/callback";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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
                        "/"
                ).permitAll()
                .anyRequest().permitAll(); // ← 여기만 바꿔주면 끝
//                .anyRequest().authenticated();

        httpSecurity
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        // 성공 핸들러
                        .successHandler((request, response, authentication) -> {
                            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

                            // JWT 발급
                            JwtToken token = jwtTokenProvider.generateToken(authentication);

                            // RefreshToken Redis 저장
                            tokenRedisRepository.save(
                                    new TokenRedis(principal.getUsername(), token.getRefreshToken())
                            );

                            // 프론트 콜백 URL로 리다이렉트 (토큰 전달)
                            String redirectUrl = FRONT_CALLBACK
                                    + "?accessToken=" + URLEncoder.encode(token.getAccessToken(), StandardCharsets.UTF_8)
                                    + "&refreshToken=" + URLEncoder.encode(token.getRefreshToken(), StandardCharsets.UTF_8);

                            response.sendRedirect(redirectUrl);
                        })
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                );

        // JWT 인증 필터 등록: UsernamePasswordAuthenticationFilter 전에 실행
        httpSecurity.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );

        // 설정 완료 후 SecurityFilterChain 반환
        return httpSecurity.build();
    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
//                .httpBasic().disable()
//                .csrf().disable()
//                // JWT를 사용하기 때문에 세션을 사용하지 않음
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeHttpRequests()
//                // 해당 API에 대해서는 모든 요청을 허가
//                .requestMatchers(
//                        "/members/login",
//                        "/members/sign-up",
//                        "/v3/api-docs/**",
//                        "/swagger-ui/**",
//                        "/swagger-resources/**",
//                        "/webjars/**",
//                        "/configuration/**",
//                        "/",
//                        "/oauth2/**",
//                        "/login/oauth2/**",
//                        "/oauth2/authorization/**"
//                ).permitAll()
//                // USER 권한이 있어야 요청할 수 있음
//                // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
//                .anyRequest().authenticated()
//                .and()
//                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class).build();
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cfg = new org.springframework.web.cors.CorsConfiguration();
        cfg.setAllowedOrigins(java.util.List.of("http://localhost:3000"));
        cfg.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
        cfg.setExposedHeaders(java.util.List.of("Authorization"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);
        return request -> cfg;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        // 패스워드 암호화 (BCrypt)
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}