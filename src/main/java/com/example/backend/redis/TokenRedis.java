package com.example.backend.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@RedisHash(value = "token", timeToLive = 60 * 60 * 24 * 7) // 리프레시토큰과 expiretime 일치
public class TokenRedis {

    @Id
    private String id;

    private String refreshToken;
}