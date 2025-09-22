package com.example.backend.auth.phone;

public interface SmsSender {
    void send(String phoneE164, String message);
}