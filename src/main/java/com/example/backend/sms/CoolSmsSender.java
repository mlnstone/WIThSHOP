package com.example.backend.sms;

import com.example.backend.auth.phone.SmsSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Primary
public class CoolSmsSender implements SmsSender {

    private final CoolSmsService coolSmsService; // 네가 만든 서비스 그대로 주입

    @Override
    public void send(String phoneE164, String message) {
        // CoolSMS는 리스트를 받으니 단건을 리스트로 래핑
        coolSmsService.sendBulkMessage(List.of(phoneE164), message);
    }
}