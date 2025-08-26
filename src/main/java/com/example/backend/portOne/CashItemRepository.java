package com.example.backend.portOne;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CashItemRepository extends JpaRepository<CashItem, Long> {
    boolean existsByImpUid(String impUid);
}