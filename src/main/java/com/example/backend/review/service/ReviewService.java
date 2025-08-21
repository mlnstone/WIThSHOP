package com.example.backend.review.service;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.menu.entity.Menu;
import com.example.backend.menu.repository.MenuRepository;
import com.example.backend.orderHistoryDetail.repository.OrderHistoryDetailRepository;
import com.example.backend.review.dto.ReviewPublicResponseDto;
import com.example.backend.review.dto.ReviewRequestDto;
import com.example.backend.review.dto.ReviewSummaryDto;
import com.example.backend.review.entity.Review;
import com.example.backend.review.repository.ReviewRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
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

    //리뷰 단건 조회
    @Transactional
    public ReviewPublicResponseDto getReview(@PathVariable Long reviewId) {
        ReviewPublicResponseDto review = reviewRepository.findReviewById(reviewId);

        if (review == null) {
            throw new IllegalArgumentException("해당 리뷰는 존재하지 않습니다.");
        }

        return review.withMaskedUserName();
    }

    // 메뉴의 리뷰 전체 조회
    public Page<ReviewPublicResponseDto> getReviewsByMenu(
            Pageable pageable, Long menuId
    ) {
        if (!menuRepository.existsById(menuId)) {
            throw new IllegalArgumentException("존재하지 않는 메뉴입니다.");
        }
        return reviewRepository.findAllByMenu_MenuId(pageable, menuId)
                .map(ReviewPublicResponseDto::withMaskedUserName);
    }

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

        // 2) 구매 이력 검증 (주문상세 기준 + 허용 상태)
        boolean purchased = orderHistoryDetailRepository.existsPurchased(userId, menuId, REVIEW_OK_STATUSES);
        if (!purchased) {
            throw new IllegalStateException("해당 상품을 구매한 사용자만 리뷰를 작성할 수 있습니다.");
        }

        // 3) (선택) 중복 리뷰 방지
        if (reviewRepository.existsByUser_UserIdAndMenu_MenuId(userId, menuId)) {
            throw new IllegalStateException("이미 이 상품에 대한 리뷰를 작성하셨습니다.");
        }

        // 4) 저장
        Review review = dto.toEntity(user, menu);
        reviewRepository.save(review);

        return ReviewPublicResponseDto.from(review).withMaskedUserName();
    }
}
