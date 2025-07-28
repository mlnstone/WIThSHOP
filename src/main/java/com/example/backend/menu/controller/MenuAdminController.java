package com.example.backend.menu.controller;

import com.example.backend.menu.dto.MenuAdminResponseDto;
import com.example.backend.menu.dto.MenuRequestDto;
import com.example.backend.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "메뉴-관리자", description = "관리자의 메뉴 컨트롤러")
@RestController
@RequiredArgsConstructor
public class MenuAdminController {

    private final MenuService menuService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "메뉴 등록")
    @PostMapping("/admin/menus")
    public ResponseEntity<MenuAdminResponseDto> createMenu(
            @RequestBody MenuRequestDto menuRequestDto,
            Principal principal
    ) {
        MenuAdminResponseDto menuAdminResponseDto = menuService.createMenu(menuRequestDto, principal);

        return ResponseEntity.ok(menuAdminResponseDto);
    }
}
