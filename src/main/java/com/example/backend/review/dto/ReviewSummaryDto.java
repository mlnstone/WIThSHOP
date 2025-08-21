package com.example.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewSummaryDto {
    private long count;
    private double average;
}