// src/main/java/com/example/backend/config/RedisConfig.java
package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password:}") // 비밀번호 없으면 빈 문자열
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName(host);
        conf.setPort(port);
        if (password != null && !password.isBlank()) {
            conf.setPassword(password);
        }
        return new LettuceConnectionFactory(conf);
    }

    // (A) 조회수 등 범용 용도
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> t = new RedisTemplate<>();
        t.setConnectionFactory(factory);
        // 문자열 직렬화(권장) — 기존에 없던 설정
        StringRedisSerializer s = new StringRedisSerializer();
        t.setKeySerializer(s);
        t.setValueSerializer(s);
        t.setHashKeySerializer(s);
        t.setHashValueSerializer(s);
        t.afterPropertiesSet();
        return t;
    }

    // (B) 번호 인증(OTP) 전용 – 가독성/분리 목적
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        // String <-> String 최적화 템플릿
        return new StringRedisTemplate(factory);
    }
}