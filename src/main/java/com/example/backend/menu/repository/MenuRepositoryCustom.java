package com.example.backend.menu.repository;

import com.example.backend.menu.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuRepositoryCustom {
    Page<Menu> findAllMenu(Pageable pageable, String search);

    Page<Menu> findAllByCategory_CategoryId(Pageable pageable, Long categoryId);
}
