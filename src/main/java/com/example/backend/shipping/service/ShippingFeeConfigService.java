package com.example.backend.shipping.service;

import com.example.backend.shipping.entity.ShippingFeeConfig;
import com.example.backend.shipping.repository.ShippingFeeConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShippingFeeConfigService {

    private final ShippingFeeConfigRepository repository;
    private static final long SINGLE_ROW_ID = 1;

    /**
     * 현재 배송비 조회 (없으면 0으로 초기화)
     */
    @Transactional
    public long currentAmount() {
        return repository.findById(SINGLE_ROW_ID)
                .orElseGet(() -> repository.save(
                        ShippingFeeConfig.builder()
                                .id(SINGLE_ROW_ID)
                                .amount(0L)
                                .build()
                ))
                .getAmount();
    }

    /**
     * 배송비 수정
     */
    @Transactional
    public long updateAmount(long amount) {
        if (amount < 0) throw new IllegalArgumentException("배송비는 0 이상이어야 합니다.");

        ShippingFeeConfig cfg = repository.findById(SINGLE_ROW_ID)
                .orElseGet(() -> ShippingFeeConfig.builder()
                        .id(SINGLE_ROW_ID)
                        .amount(0L)
                        .build());

        cfg.setAmount(amount);
        repository.save(cfg);
        return cfg.getAmount();
    }

    @Transactional
    public void ensureRow() {
        repository.findById(SINGLE_ROW_ID).orElseGet(() ->
                repository.save(ShippingFeeConfig.builder()
                        .id(SINGLE_ROW_ID)
                        .amount(0L)
                        .build())
        );
    }
}