package com.example.backend.menu.dto;

import com.example.backend.category.entity.Category;
import com.example.backend.common.enums.MenuStatus;
import com.example.backend.menu.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuRequestDto {

    private String menuName;
    private String description;
    private Long costPrice;
    private Long originalPrice;
    private Long salePrice;
    private MenuStatus status;
    private String image;
    private String detail;
    private Long stock;
    private Long categoryId; // 👉 카테고리 엔티티는 서비스에서 직접 조회해서 넣을 예정

    public Menu toEntity(Category category) {
        return Menu.builder()
                .menuName(menuName)
                .description(description)
                .costPrice(costPrice)
                .originalPrice(originalPrice)
                .salePrice(salePrice)
                .status(status)
                .image(image)
                .detail(detail)
                .stock(stock)
                .category(category)
                .build();
    }
}