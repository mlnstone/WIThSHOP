package com.example.backend.coupon.repository;

import com.example.backend.coupon.entity.Coupon;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
                update Coupon c
                   set c.limitQuantity = c.limitQuantity - 1
                 where c.couponId = :couponId
                   and c.limitQuantity is not null
                   and c.limitQuantity > 0
            """)
    int decreaseQuantityIfAvailable(@Param("couponId") Long couponId);
}