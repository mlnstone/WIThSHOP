package com.example.backend.exception;

public record ApiErrorResponseDto(int code, String status, String message) {}
