package com.example.backend.menu.controller;

import com.example.backend.menu.dto.MenuAllResponseDto;
import com.example.backend.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "메뉴-일반사용자", description = "일반 사용자의 메뉴 컨트롤러")
@RestController
@RequiredArgsConstructor
public class MenuPublicController {
    private final MenuService menuService;

    @Operation(summary = "메뉴 전체 조회 + 검색", description = "")
    @GetMapping("/menus")
    public ResponseEntity<Page<MenuAllResponseDto>> getAllMenus(
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) String search
    ) {
        Page<MenuAllResponseDto> menuResponseDto = menuService.getAllMenu(pageable, search);
        return ResponseEntity.ok(menuResponseDto);
    }

    @Operation(summary = "카테고리로 메뉴 조회")
    @GetMapping("/categories/{categoryId}/menus")
    public ResponseEntity<Page<MenuAllResponseDto>> getMenusByCategory(
            @PageableDefault Pageable pageable,
            @PathVariable Long categoryId
    ) {
        Page<MenuAllResponseDto> menu = menuService.getMenusByCategory(pageable, categoryId);
        return ResponseEntity.ok(menu);
    }

    @Operation(summary = "메뉴 단일 조회")
    @GetMapping("/menus/{menuId}")
    public ResponseEntity<MenuAllResponseDto> getMenu(
            @PathVariable Long menuId
    ) {
        MenuAllResponseDto menuResponseDto = menuService.getMenu(menuId);
        return ResponseEntity.status(HttpStatus.OK).body(menuResponseDto);
    }
}
