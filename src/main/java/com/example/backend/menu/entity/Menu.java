package com.example.backend.menu.entity;

import com.example.backend.category.entity.Category;
import com.example.backend.common.enums.MenuStatus;
import com.example.backend.menu.dto.MenuRequestDto;
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

    public void updateMenu(MenuRequestDto dto, Category category) {
        this.menuName = dto.getMenuName();
        this.description = dto.getDescription();
        this.costPrice = dto.getCostPrice();
        this.originalPrice = dto.getOriginalPrice();
        this.salePrice = dto.getSalePrice();
        this.status = dto.getStatus();
        this.image = dto.getImage();
        this.detail = dto.getDetail();
        this.stock = dto.getStock();
        this.category = category;
    }

    public void decreaseStock(long qty) {
        if (qty <= 0) throw new IllegalArgumentException("수량은 1 이상");
        if (this.stock < qty) throw new IllegalArgumentException("재고 부족");
        this.stock -= qty;
    }

    public void increaseStock(long qty) {
        if (qty <= 0) throw new IllegalArgumentException("수량 오류");
        this.stock += qty;
    }
}