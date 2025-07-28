package com.example.backend.menu.dto;

import com.example.backend.common.enums.MenuStatus;
import com.example.backend.menu.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuAdminResponseDto {
    private Long menuId;
    private String menuName;
    private String description;
    private Long costPrice; // 원가
    private Long originalPrice; // 정가
    private Long salePrice; // 판매(할인가)
    private String image;
    private String detail;
    private Long stock;
    private String categoryName;
    private MenuStatus status;

    public static MenuAdminResponseDto from(Menu menu) {
        return new MenuAdminResponseDto(
                menu.getMenuId(),
                menu.getMenuName(),
                menu.getDescription(),
                menu.getCostPrice(),
                menu.getOriginalPrice(),
                menu.getSalePrice(),
                menu.getImage(),
                menu.getDetail(),
                menu.getStock(),
                menu.getCategory().getCategoryName(),
                menu.getStatus()
        );
    }
}
