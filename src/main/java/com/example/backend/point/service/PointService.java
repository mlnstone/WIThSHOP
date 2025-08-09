package com.example.backend.point.service;

import com.example.backend.point.dto.PointBalanceResponse;
import com.example.backend.point.entity.Point;
import com.example.backend.point.repository.PointRepository;
import com.example.backend.pointTransaction.dto.PointTransactionResponse;
import com.example.backend.pointTransaction.entity.PointTransaction;
import com.example.backend.pointTransaction.repository.PointTransactionRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointTransactionRepository pointTransactionRepository;

    // 본인 적립금, 내역
    public PointBalanceResponse getMyBalance(Principal principal) {
        Point point = getCurrentPoint(principal);
        return PointBalanceResponse.from(point);
    }

    public Page<PointTransactionResponse> getMyTransactions(Principal principal, Pageable pageable) {
        Point point = getCurrentPoint(principal);
        return pointTransactionRepository.findByPoint(point, pageable)
                .map(PointTransactionResponse::from);
    }

    // 관리자 - 특정 유저 조회 적립금, 내역
    public PointBalanceResponse getUserBalance(Long userId) {
        Point point = getPointByUserId(userId);
        return PointBalanceResponse.from(point);
    }

    public Page<PointTransactionResponse> getUserTransactions(Long userId, Pageable pageable) {
        Point point = getPointByUserId(userId);
        return pointTransactionRepository.findByPoint(point, pageable)
                .map(PointTransactionResponse::from);
    }

    // 잔액 '설정'
    @Transactional
    public PointBalanceResponse changePoint(Long userId, Long amount, String description) {
        if (amount < 0) throw new IllegalArgumentException("포인트 잔액은 음수일 수 없습니다.");

        Point point = getPointByUserIdForUpdate(userId); // ← 락 잡음

        long before = point.getBalance();
        long delta = amount - before;

        point.add(delta);

        pointTransactionRepository.save(PointTransaction.builder()
                .point(point)
                .amount(delta)
                .description(description)
                .build());

        return PointBalanceResponse.from(point);
    }

    // 증/차감
    @Transactional
    public PointBalanceResponse adjustPoint(Long userId, Long amount, String description) {
        Point point = getPointByUserIdForUpdate(userId); // ← 락 잡음


        if (point.getBalance() + amount < 0) throw new IllegalArgumentException("포인트가 부족합니다.");

        point.add(amount);

        pointTransactionRepository.save(PointTransaction.builder()
                .point(point)
                .amount(amount)
                .description(description)
                .build());

        return PointBalanceResponse.from(point);
    }

    // 메서드
    private Point getCurrentPoint(Principal principal) {
        User user = getCurrentUser(principal);
        return pointRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));
    }

    private User getCurrentUser(Principal principal) {
        String email = principal.getName();
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private Point getPointByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return pointRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));
    }

    // 비관적 락
    private Point getPointByUserIdForUpdate(Long userId) {
        return pointRepository.findForUpdateByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));
    }
}