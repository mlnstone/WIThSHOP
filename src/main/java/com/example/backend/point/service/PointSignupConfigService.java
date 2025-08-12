package com.example.backend.point.service;


import com.example.backend.point.entity.PointSignupConfig;
import com.example.backend.point.repository.PointSignupConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointSignupConfigService {

    private final PointSignupConfigRepository pointSignupConfigRepository;

    private static final long SINGLE_ROW_ID = 1;

    /**
     * 현재 가입 적립금 금액 조회 (행이 없으면 0으로 초기화 후 반환)
     */
    @Transactional
    public long currentAmount() {
        return pointSignupConfigRepository.findById(SINGLE_ROW_ID)
                .orElseGet(() -> pointSignupConfigRepository.save(
                        PointSignupConfig.builder()
                                .id(SINGLE_ROW_ID)
                                .amount(0L)
                                .build()
                ))
                .getAmount();
    }

    /**
     * 가입 적립금 금액 설정/수정 (음수 방지)
     */
    @Transactional
    public long updateAmount(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("가입 적립금은 0 이상이어야 합니다.");
        }

        PointSignupConfig cfg = pointSignupConfigRepository.findById(SINGLE_ROW_ID)
                .orElseGet(() -> PointSignupConfig.builder()
                        .id(SINGLE_ROW_ID)
                        .amount(0L)
                        .build()
                );

        cfg.setAmount(amount);
        pointSignupConfigRepository.save(cfg);
        return cfg.getAmount();
    }

    @Transactional
    public void ensureRow() {
        pointSignupConfigRepository.findById(SINGLE_ROW_ID).orElseGet(() ->
                pointSignupConfigRepository.save(PointSignupConfig.builder()
                        .id(SINGLE_ROW_ID)
                        .amount(0L)
                        .build())
        );
    }
}