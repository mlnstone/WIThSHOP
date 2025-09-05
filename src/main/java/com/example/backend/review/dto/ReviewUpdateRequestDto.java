package com.example.backend.review.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewUpdateRequestDto {
    @NotBlank
    @Size(max = 255)
    private String reviewTitle;

    @NotBlank
    @Size(max = 2000)
    private String reviewContent;

    @Size(max = 512)
    private String reviewImage;

    @NotNull
    @Min(1)
    @Max(5)
    private Double rating;
}