package com.example.backend.userCoupon.repository;

import com.example.backend.user.entity.User;
import com.example.backend.userCoupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, String> {

    long countByCoupon_CouponIdAndUser(Long couponId, User user);

    @Query("""
                select uc
                from UserCoupon uc
                join fetch uc.coupon c
                where uc.user.userId = :userId
            """)
    List<UserCoupon> findWithCouponByUserId(@Param("userId") Long userId);

    @Query("""
                select uc
                from UserCoupon uc
                join fetch uc.coupon c
                where uc.userCouponId = :userCouponId
                  and uc.user.userId = :userId
            """)
    Optional<UserCoupon> findWithCouponByIdAndUserId(
            @Param("userCouponId") String userCouponId,
            @Param("userId") Long userId
    );
}