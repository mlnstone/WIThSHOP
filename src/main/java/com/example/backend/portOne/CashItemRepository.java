package com.example.backend.portOne;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CashItemRepository extends JpaRepository<CashItem, Long> {
    boolean existsByImpUid(String impUid);

    Optional<CashItem> findByImpUid(String impUid);

    Optional<CashItem> findByMerchantUid(String merchantUid);

    Optional<CashItem> findByOrderCode(String orderCode);

}