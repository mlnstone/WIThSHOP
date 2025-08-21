package com.example.backend.review.repository;

import com.example.backend.review.dto.ReviewSummaryDto;
import com.example.backend.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    @Query("""
                select new com.example.backend.review.dto.ReviewSummaryDto(
                    count(r), coalesce(avg(r.rating), 0.0)
                )
                from Review r
                where r.menu.menuId = :menuId
            """)
    ReviewSummaryDto countAndAvgByMenuId(@Param("menuId") Long menuId);

    boolean existsByUser_UserIdAndMenu_MenuId(Long userId, Long menuId);
}
