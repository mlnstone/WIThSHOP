package com.example.backend.post.entity;

import com.example.backend.common.base.EntityDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "post")
public class Post extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String postTitle;

    @Lob
    @Column(nullable = false)
    private String postContent;

    private Long hit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_type_id", nullable = false)
    private PostType postType;
}