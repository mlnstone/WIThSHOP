package com.example.backend.category.dto;

import com.example.backend.category.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryRequestDto {

    private String categoryName;

    public Category toEntity() {
        return Category.builder()
                .categoryName(categoryName)
                .build();
    }
}