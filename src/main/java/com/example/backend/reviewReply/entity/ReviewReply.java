package com.example.backend.reviewReply.entity;

import com.example.backend.common.base.EntityDate;
import com.example.backend.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_reply")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewReply extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewReplyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false, unique = true) // 🔥 유니크 필수!
    private Review review;

    @Lob
    @Column(nullable = false)
    private String reviewReplyContent;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public void updateContent(String content) {
        this.reviewReplyContent = content;
    }
}
