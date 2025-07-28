package com.example.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("에러 발생: " + ex.getMessage());
    }

    // hasRole
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("권한이 없습니다.");
    }

    // valid
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//
//        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
//            errors.put(error.getField(), error.getDefaultMessage());
//        }
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(errors);
//    }

    //valid
    // json, 에러코드, 상태, 메시지
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDto> handleException(MethodArgumentNotValidException e) {
        StringBuilder errors = new StringBuilder();

        // 예외 메시지를 로그로 기록
        log.error("MethodArgumentNotValidException 발생: {}", e.getMessage());

        // 유효성 검사 실패한 필드와 메시지를 문자열로 조합
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors
                    .append(fieldError.getField())   // 오류 발생 필드명
                    .append(" (")
                    .append(fieldError.getDefaultMessage()) // 오류 메시지
                    .append("), ");
        }

        // 마지막 쉼표 제거
        if (!errors.isEmpty()) {
            errors.replace(errors.lastIndexOf(","), errors.length(), "");
        }

        return new ResponseEntity<>(
                new ApiErrorResponseDto(
                        HttpStatus.BAD_REQUEST.value(),  // HTTP 상태 코드 (400)
                        HttpStatus.BAD_REQUEST.name(),   // 상태 이름 ("BAD_REQUEST")
                        errors.toString()               // 조합된 오류 메시지
                ),
                HttpStatus.BAD_REQUEST  // ResponseEntity 의 상태 코드 설정
        );
    }
}
