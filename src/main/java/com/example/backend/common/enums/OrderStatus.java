package com.example.backend.common.enums;

public enum OrderStatus {
    REQUESTED,   // 주문 요청 (결제 대기 상태)
    APPROVED,    // 승인됨 (결제 완료)
    REJECTED,    // 거절됨 (결제 실패 or 관리자 거절)
    SHIPPED,     // 배송 중
    DELIVERED,   // 배송 완료
    CANCELED     // 주문 취소
}