package com.example.backend.review.service;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.menu.entity.Menu;
import com.example.backend.menu.repository.MenuRepository;
import com.example.backend.orderHistoryDetail.repository.OrderHistoryDetailRepository;
import com.example.backend.review.dto.ReviewPublicResponseDto;
import com.example.backend.review.dto.ReviewRequestDto;
import com.example.backend.review.dto.ReviewSummaryDto;
import com.example.backend.review.dto.ReviewUpdateRequestDto;
import com.example.backend.review.entity.Review;
import com.example.backend.review.repository.ReviewRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;
    private final OrderHistoryDetailRepository orderHistoryDetailRepository;

    private static final EnumSet<OrderStatus> REVIEW_OK_STATUSES =
            EnumSet.of(OrderStatus.APPROVED, OrderStatus.SHIPPED, OrderStatus.DELIVERED);

    @Transactional(readOnly = true)
    public ReviewPublicResponseDto getReview(Long reviewId) {
        var review = reviewRepository.findReviewById(reviewId);
        if (review == null) throw new IllegalArgumentException("해당 리뷰는 존재하지 않습니다.");
        return review.withMaskedUserName();
    }

    @Transactional(readOnly = true)
    public Page<ReviewPublicResponseDto> getReviewsByMenu(Pageable pageable, Long menuId) {
        if (!menuRepository.existsById(menuId))
            throw new IllegalArgumentException("존재하지 않는 메뉴입니다.");
        return reviewRepository.findAllByMenu_MenuId(pageable, menuId)
                .map(ReviewPublicResponseDto::withMaskedUserName);
    }

    @Transactional(readOnly = true)
    public ReviewSummaryDto getReviewSummary(Long menuId) {
        return reviewRepository.countAndAvgByMenuId(menuId);
    }

    @Transactional
    public ReviewPublicResponseDto createReview(Principal principal, ReviewRequestDto dto) {
        // 1) 유저/메뉴 로드
        User user = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Menu menu = menuRepository.findById(dto.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        Long userId = user.getUserId();
        Long menuId = menu.getMenuId();
        String orderCode = dto.getOrderCode();

        if (orderCode == null || orderCode.isBlank()) {
            throw new IllegalArgumentException("주문번호가 필요합니다.");
        }

        // 2) 구매 이력 검증: 유저+메뉴+주문코드 + 허용상태
        boolean purchased = orderHistoryDetailRepository
                .existsPurchasedInOrder(userId, menuId, orderCode, REVIEW_OK_STATUSES);
        if (!purchased) {
            throw new IllegalStateException("해당 주문으로 구매한 사용자만 리뷰를 작성할 수 있습니다.");
        }

        // 3) 중복 리뷰 방지: 유저+메뉴+주문코드 기준
        boolean duplicated = reviewRepository
                .existsByUser_UserIdAndMenu_MenuIdAndOrderCode(userId, menuId, orderCode);
        if (duplicated) {
            throw new IllegalStateException("이미 이 주문에 대해 리뷰를 작성하셨습니다.");
        }

        // 4) 저장
        Review review = dto.toEntity(user, menu);
        reviewRepository.save(review);

        return ReviewPublicResponseDto.from(review).withMaskedUserName();
    }

    @Transactional(readOnly = true)
    public boolean hasMyReview(Principal principal, Long menuId, String orderCode) {
        User user = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (!menuRepository.existsById(menuId))
            throw new IllegalArgumentException("존재하지 않는 메뉴입니다.");
        if (orderCode == null || orderCode.isBlank())
            throw new IllegalArgumentException("주문번호가 필요합니다.");

        return reviewRepository.existsByUser_UserIdAndMenu_MenuIdAndOrderCode(
                user.getUserId(), menuId, orderCode
        );
    }

    @Transactional(readOnly = true)
    public Page<ReviewPublicResponseDto> getMyReviews(Principal principal, Pageable pageable) {
        User user = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 내 리뷰이므로 마스킹 불필요
        return reviewRepository.findAllByUserId(pageable, user.getUserId());
    }

    @Transactional(readOnly = true)
    public ReviewPublicResponseDto getMyReview(Principal principal, Long reviewId) {
        User user = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Review review = reviewRepository.findByReviewIdAndUser_UserId(reviewId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없거나 내 리뷰가 아닙니다."));

        return ReviewPublicResponseDto.from(review);
    }

    @Transactional
    public ReviewPublicResponseDto updateMyReview(Principal principal, Long reviewId, ReviewUpdateRequestDto dto) {
        User user = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Review review = reviewRepository.findByReviewIdAndUser_UserId(reviewId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없거나 내 리뷰가 아닙니다."));

        // 작성 후 3일 제한
        if (isOver3Days(review.getCreatedAt())) {
            throw new IllegalStateException("리뷰는 작성 후 3일 이내에만 수정할 수 있습니다.");
        }

        // 부분 업데이트
        review.update(dto.getReviewTitle(), dto.getReviewContent(), dto.getReviewImage(), dto.getRating());

        // 엔티티에 setter가 없다면, Review에 변경 메서드 추가하거나 @Builder(toBuilder=true) 사용
        // (예: review.update(title, content, image, rating))

        return ReviewPublicResponseDto.from(review);
    }

    @Transactional
    public void deleteMyReview(Principal principal, Long reviewId) {
        User user = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Review review = reviewRepository.findByReviewIdAndUser_UserId(reviewId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없거나 내 리뷰가 아닙니다."));

        if (isOver3Days(review.getCreatedAt())) {
            throw new IllegalStateException("리뷰는 작성 후 3일 이내에만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    private boolean isOver3Days(LocalDateTime createdAt) {
        return LocalDateTime.now().isAfter(createdAt.plusDays(3));
    }
}