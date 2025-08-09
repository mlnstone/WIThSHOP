package com.example.backend.point;

import com.example.backend.common.enums.Role;
import com.example.backend.common.enums.UserProvider;
import com.example.backend.point.entity.Point;
import com.example.backend.point.repository.PointRepository;
import com.example.backend.point.service.PointService;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointConcurrencyTest {

    @Autowired
    PointService pointService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PointRepository pointRepository;

    Long userId;

    @BeforeEach
    void setUp() {
        // 유니크 보장용 랜덤 이메일 (기존 데이터 충돌 방지)
        String email = "concurrency+" + UUID.randomUUID() + "@test.com";

        // 테스트 유저 + 포인트 100 세팅
        User user = userRepository.save(
                User.builder()
                        .userEmail(email)
                        .userPwd("pw")
                        .userType(Role.CUSTOMER)
                        .userProvider(UserProvider.LOCAL)
                        .build()
        );

        Point point = pointRepository.findByUser(user)
                .orElseGet(() -> pointRepository.save(
                        Point.builder().user(user).balance(100L).build()
                ));
        pointRepository.save(point);

        userId = user.getUserId();
    }

    @Test
    void concurrent_adjust_minus1_hundred_times_should_be_zero_without_lock_may_flake() throws InterruptedException {
        int threads = 32;
        int executeCount = 100;

        var pool = Executors.newFixedThreadPool(threads);
        var ready = new CountDownLatch(executeCount); // 시작선 정렬
        var start = new CountDownLatch(1);            // 동시에 출발
        var done = new CountDownLatch(executeCount); // 모두 끝날 때까지 대기

        for (int i = 0; i < executeCount; i++) {
            pool.submit(() -> {
                try {
                    ready.countDown();
                    start.await(); // 동시에 시작
                    // 동일 유저에 대해 -1 차감 (락 없음)
                    pointService.adjustPoint(userId, -1L, "concurrency test");
                } catch (Exception ignored) {
                } finally {
                    done.countDown();
                }
            });
        }

        // 모든 작업 준비되면 일제히 출발
        ready.await();
        start.countDown();
        done.await();
        pool.shutdown();

        // 최종 잔액 조회 (락 없는 일반 조회)
        User user = userRepository.findById(userId).orElseThrow();
        long balance = pointRepository.findByUser(user)
                .orElseThrow()
                .getBalance();

        // 이 테스트는 락이 없어서 간헐적으로 실패(0이 아닐 수 있음)할 수 있습니다.
        // 동시성 문제를 '보여주는' 목적이라면 아래 assert를 유지하세요.
        assertThat(balance).isEqualTo(0L);
    }
}