package com.example.backend.category.service;

import com.example.backend.category.dto.CategoryRequestDto;
import com.example.backend.category.dto.CategoryResponseDto;
import com.example.backend.category.entity.Category;
import com.example.backend.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));
        return CategoryResponseDto.from(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        Category category = requestDto.toEntity();
        return CategoryResponseDto.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        category.updateName(requestDto.getCategoryName());
        return CategoryResponseDto.from(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));
        categoryRepository.delete(category);
    }
}