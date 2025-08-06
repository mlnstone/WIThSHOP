package com.example.backend.post.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "post_type")
public class PostType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postTypeId;

    @Column(length = 50)
    private String title;
}