package com.example.backend.menu.controller;

import com.example.backend.menu.dto.MenuAdminResponseDto;
import com.example.backend.menu.dto.MenuRequestDto;
import com.example.backend.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "메뉴-관리자", description = "관리자의 메뉴 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class MenuAdminController {

    private final MenuService menuService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "메뉴 등록")
    @PostMapping("/menus")
    public ResponseEntity<MenuAdminResponseDto> createMenu(
            @RequestBody MenuRequestDto menuRequestDto,
            Principal principal
    ) {
        MenuAdminResponseDto menuAdminResponseDto = menuService.createMenu(menuRequestDto, principal);

        return ResponseEntity.ok(menuAdminResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "메뉴 삭제")
    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<String> deleteMenu(
            @PathVariable Long menuId,
            Principal principal
    ) {
        menuService.deleteMenu(menuId, principal);
        return ResponseEntity.status(HttpStatus.OK).body("메뉴가 삭제되었습니다.");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "메뉴 수정")
    @PostMapping("/menus/{menuId}")
    public ResponseEntity<MenuAdminResponseDto> updateMenu(
            @PathVariable Long menuId,
            @RequestBody MenuRequestDto menuRequestDto,
            Principal principal
    ) {
        MenuAdminResponseDto menuAdminResponseDto = menuService.updateMenu(menuId, menuRequestDto, principal);

        return ResponseEntity.ok(menuAdminResponseDto);
    }
}
