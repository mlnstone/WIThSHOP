package com.example.backend.menu.service;

import com.example.backend.category.entity.Category;
import com.example.backend.category.repository.CategoryRepository;
import com.example.backend.menu.dto.MenuAdminResponseDto;
import com.example.backend.menu.dto.MenuPublicResponseDto;
import com.example.backend.menu.dto.MenuRequestDto;
import com.example.backend.menu.entity.Menu;
import com.example.backend.menu.repository.MenuRepository;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Page<MenuPublicResponseDto> getAllMenu(Pageable pageable, String search) {
        Page<Menu> menu = menuRepository.findAllMenu(pageable, search);

        return menu.map(MenuPublicResponseDto::from);
    }

    public Page<MenuPublicResponseDto> getMenusByCategory(Pageable pageable, Long categoryId) {
        Page<Menu> menus = menuRepository.findAllByCategory_CategoryId(pageable, categoryId);
        return menus.map(MenuPublicResponseDto::from);
    }

    public MenuPublicResponseDto getMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다: " + menuId));

        return MenuPublicResponseDto.from(menu);
    }

    @Transactional
    public MenuAdminResponseDto createMenu(MenuRequestDto menuRequestDto) {
        Category category = categoryRepository.findById(menuRequestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));

        Menu menu = menuRequestDto.toEntity(category);
        menuRepository.save(menu);
        return MenuAdminResponseDto.from(menu);
    }

    @Transactional
    public void deleteMenu(Long menuId) {
        menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 없습니다."));
        menuRepository.deleteById(menuId);
    }

    @Transactional
    public MenuAdminResponseDto updateMenu(Long menuId, MenuRequestDto menuRequestDto) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 없습니다."));

        Category category = categoryRepository.findById(menuRequestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));

        menu.updateMenu(menuRequestDto, category);

        return MenuAdminResponseDto.from(menu);
    }
}
