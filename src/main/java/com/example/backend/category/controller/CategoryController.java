package com.example.backend.category.controller;

import com.example.backend.category.dto.CategoryRequestDto;
import com.example.backend.category.dto.CategoryResponseDto;
import com.example.backend.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관리자-카테고리", description = "관리자 - 카테고리")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategory(categoryId));
    }

    @Operation(summary = "카테고리 전체 조회")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "카테고리 등록")
    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity.ok(categoryService.createCategory(requestDto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "카테고리 수정")
    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long categoryId,
                                                              @RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, requestDto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "카테고리 삭제")
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok("카테고리가 삭제되었습니다.");
    }
}

