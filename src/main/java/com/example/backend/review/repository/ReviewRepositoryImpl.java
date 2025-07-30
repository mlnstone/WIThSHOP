package com.example.backend.review.repository;

import com.example.backend.review.entity.QReview;
import com.example.backend.review.entity.Review;
import com.example.backend.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Review> findAllByMenu_MenuId(Pageable pageable, Long menuId) {
        QReview review = QReview.review;
        QUser user = QUser.user;

        List<Review> content = queryFactory
                .selectFrom(review)
                .join(review.user, user).fetchJoin()
                .leftJoin(user.point).fetchJoin()
                .where(review.menu.menuId.eq(menuId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(review.count())
                .from(review)
                .where(review.menu.menuId.eq(menuId))
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }
}