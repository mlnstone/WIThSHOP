package com.example.backend.menu.entity;

import com.example.backend.category.entity.Category;
import com.example.backend.common.enums.MenuStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Column(nullable = false)
    private String menuName;

    private String description;

    @Column(nullable = false)
    private Long costPrice;

    @Column(nullable = false)
    private Long originalPrice;

    @Column(nullable = false)
    private Long salePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MenuStatus status;

    private String image;

    @Lob
    private String detail;

    @Column(nullable = false)
    private Long stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}