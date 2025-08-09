package com.example.backend.point.repository;

import com.example.backend.point.entity.Point;
import com.example.backend.user.entity.User;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUser(User user);

    boolean existsByUser(User user);

    // 5초 대기(옵션)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Point p where p.user.userId = :userId")
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"))
    Optional<Point> findForUpdateByUserId(Long userId);
}