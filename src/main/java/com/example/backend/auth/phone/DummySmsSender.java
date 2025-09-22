package com.example.backend.auth.phone;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DummySmsSender implements SmsSender {
    @Override
    public void send(String phoneE164, String message) {
        log.info("[SMS] to={} message={}", phoneE164, message);
    }
}