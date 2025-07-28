package com.example.backend.config;

import com.example.backend.auth.JwtAuthenticationFilter;
import com.example.backend.auth.JwtToken;
import com.example.backend.auth.JwtTokenProvider;
import com.example.backend.auth.service.CustomOAuth2UserService;
import com.example.backend.redis.TokenRedis;
import com.example.backend.redis.TokenRedisRepository;
import com.example.backend.user.entity.PrincipalDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRedisRepository tokenRedisRepository;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/members/login",
                        "/members/sign-up",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/configuration/**",
                        "/oauth2/**",
                        "/login/oauth2/**",
                        "/oauth2/authorization/**"
                ).permitAll()
                .anyRequest().permitAll(); // ← 여기만 바꿔주면 끝
//                .anyRequest().authenticated();

        httpSecurity
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler((request, response, authentication) -> {
                            // OAuth2 로그인 성공 후 처리
                            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

                            // JWT 토큰 생성
                            JwtToken token = jwtTokenProvider.generateToken(authentication);

                            // 레디스에 저장
                            tokenRedisRepository.save(
                                    new TokenRedis(
                                            principal.getUsername(),
                                            token.getAccessToken(),
                                            token.getRefreshToken()
                                    )
                            );

                            // 응답을 JSON으로
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(new ObjectMapper().writeValueAsString(token));
                        })
                );

        // JWT 인증 필터 등록: UsernamePasswordAuthenticationFilter 전에 실행
        httpSecurity
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

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
    public PasswordEncoder passwordEncoder() {
        // 패스워드 암호화 (BCrypt)
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}