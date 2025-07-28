package com.example.backend.menu.service;

import com.example.backend.category.entity.Category;
import com.example.backend.category.repository.CategoryRepository;
import com.example.backend.common.enums.Role;
import com.example.backend.menu.dto.MenuAdminResponseDto;
import com.example.backend.menu.dto.MenuAllResponseDto;
import com.example.backend.menu.dto.MenuRequestDto;
import com.example.backend.menu.entity.Menu;
import com.example.backend.menu.repository.MenuRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Page<MenuAllResponseDto> getAllMenu(Pageable pageable, String search) {
        Page<Menu> menu = menuRepository.findAllMenu(pageable, search);

        return menu.map(MenuAllResponseDto::from);
    }

    public Page<MenuAllResponseDto> getMenusByCategory(Pageable pageable, Long categoryId) {
        Page<Menu> menus = menuRepository.findAllByCategory_CategoryId(pageable, categoryId);
        return menus.map(MenuAllResponseDto::from);
    }

    public MenuAllResponseDto getMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다: " + menuId));

        return MenuAllResponseDto.from(menu);
    }

    @Transactional
    public MenuAdminResponseDto createMenu(MenuRequestDto menuRequestDto, Principal principal) {
        validateAdminUser(principal);
        Category category = categoryRepository.findById(menuRequestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));

        Menu menu = menuRequestDto.toEntity(category);
        menuRepository.save(menu);
        return MenuAdminResponseDto.from(menu);
    }

    // 관리자 체크
    private void validateAdminUser(Principal principal) {
        User user = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다"));

        if (user.getUserType() != Role.ADMIN) {
            throw new AccessDeniedException("관리자만 가능한 작업입니다.");
        }
    }
}
