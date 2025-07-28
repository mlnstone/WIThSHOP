package com.example.backend.menu.repository;

import com.example.backend.menu.entity.Menu;
import com.example.backend.menu.entity.QMenu;
import com.example.backend.category.entity.QCategory;
import com.example.backend.common.enums.MenuStatus;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Menu> findAllMenu(Pageable pageable, String search) {
        QMenu menu = QMenu.menu;
        QCategory category = QCategory.category;

        BooleanBuilder builder = new BooleanBuilder();

        // 검색 조건
        if (search != null && !search.isBlank()) {
            builder.and(
                    menu.menuName.containsIgnoreCase(search)
                            .or(menu.description.containsIgnoreCase(search))
            );
        }

        // status가 ACTIVE일 때
        builder.and(menu.status.eq(MenuStatus.ACTIVE));

        List<Menu> content = queryFactory
                .selectFrom(menu)
                .leftJoin(menu.category, category).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(menu.menuId.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(menu.count())
                .from(menu)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Menu> findAllByCategory_CategoryId(Pageable pageable, Long categoryId) {
        QMenu menu = QMenu.menu;
        QCategory category = QCategory.category;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(category.categoryId.eq(categoryId));
        builder.and(menu.status.eq(MenuStatus.ACTIVE)); // status가 ACTIVE일 때

        List<Menu> content = queryFactory
                .selectFrom(menu)
                .leftJoin(menu.category, category).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(menu.menuId.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(menu.count())
                .from(menu)
                .leftJoin(menu.category, category)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}