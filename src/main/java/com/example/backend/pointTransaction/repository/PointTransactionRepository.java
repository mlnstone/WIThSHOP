package com.example.backend.pointTransaction.repository;

import com.example.backend.point.entity.Point;
import com.example.backend.pointTransaction.entity.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    Page<PointTransaction> findByPoint(Point point, Pageable pageable);
}
