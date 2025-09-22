package com.example.backend.auth.phone;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PhoneVerificationService {

    private final StringRedisTemplate redis;
    private final SmsSender smsSender;

    public boolean verifyCodeForSignup(String phone, String code) {
        String raw = normalizePhone(phone);
        String otpKey = otpKey(raw);

        String v = redis.opsForValue().get(otpKey);
        if (v == null) return false;

        String[] parts = v.split(":");
        if (parts.length != 2) return false;

        String savedCode = parts[0];
        if (!savedCode.equals(code)) {
            return false;
        }

        // 성공 → Redis에서 키 삭제
        redis.delete(otpKey);
        return true;
    }

    /**
     * 010 번호만 처리
     */
    private static String normalizePhone(String input) {
        String digits = input.replaceAll("[^0-9]", ""); // 숫자만 남김
        if (digits.startsWith("010")) {
            return "+82" + digits.substring(1); // 01012345678 → +821012345678
        }
        throw new IllegalArgumentException("지원하지 않는 전화번호 형식입니다.");
    }

    /**
     * Redis Key 생성
     */
    private static String otpKey(String phone) {
        return "otp:phone:" + phone; // e.g. otp:phone:+821012345678
    }

    public void requestCodeForSignup(String phone) {
        String raw = normalizePhone(phone);
        // 60초 쿨다운 키(선택)
        String coolKey = "otp:cooldown:" + raw;
        if (Boolean.TRUE.equals(redis.hasKey(coolKey))) {
            // 이미 보낸 지 60초 안 됨
            throw new IllegalStateException("잠시 후 다시 시도해주세요.");
        }

        String otpKey = otpKey(raw);
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1000000));
        // 저장 형식: code:remainAttempts
        redis.opsForValue().set(otpKey, code + ":5", Duration.ofSeconds(180)); // 3분 TTL
        // 쿨다운 60초
        redis.opsForValue().set(coolKey, "1", Duration.ofSeconds(60));

        // 실제 전송
        String msg = "[WIThSHOP] 인증번호 " + code + " (3분 이내 유효)";
        smsSender.send(raw, msg);
    }
}