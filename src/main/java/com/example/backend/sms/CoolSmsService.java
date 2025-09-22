// src/main/java/com/example/backend/sms/CoolSmsService.java
package com.example.backend.sms;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoolSmsService {

    private final DefaultMessageService messageService;
    private final String fromNumber;

    public CoolSmsService(
            @Value("${coolsms.apikey}") String apiKey,
            @Value("${coolsms.apisecret}") String apiSecret,
            @Value("${coolsms.fromnumber}") String fromNumber
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        this.fromNumber = fromNumber;
    }

    public void sendBulkMessage(List<String> phoneNumbers, String content) {
        List<Message> messages = new ArrayList<>();
        for (String to : phoneNumbers) {
            Message m = new Message();
            m.setFrom(fromNumber);
            m.setTo(to);      // "+8210..." 그대로 전달
            m.setText(content);
            messages.add(m);
        }

        try {
            this.messageService.send(messages, false, true);
        } catch (NurigoMessageNotReceivedException e) {
            System.out.println("실패한 메시지 목록: " + e.getFailedMessageList());
            
            throw new RuntimeException("일부 메시지 전송 실패", e);  // ✅ 런타임 예외로 감싸기
        } catch (Exception e) {
            System.out.println("에러: " + e.getMessage());
            throw new RuntimeException("SMS 전송 중 오류", e);  // ✅ 런타임 예외로 감싸기
        }
    }
}