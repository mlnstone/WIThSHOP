package com.example.backend.menu.dto;

import com.example.backend.menu.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuAllResponseDto {
    private Long menuId;
    private String menuName;
    private String description;
    private Long originalPrice; // 정가
    private Long salePrice; // 판매가
    private String image;
    private String detail;
    private String categoryName;

    public static MenuAllResponseDto from(Menu menu) {
        return new MenuAllResponseDto(
                menu.getMenuId(),
                menu.getMenuName(),
                menu.getDescription(),
                menu.getOriginalPrice(),
                menu.getSalePrice(),
                menu.getImage(),
                menu.getDetail(),
                menu.getCategory().getCategoryName()
        );
    }
}
