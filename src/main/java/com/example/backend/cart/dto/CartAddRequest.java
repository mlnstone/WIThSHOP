package com.example.backend.cart.dto;

import lombok.Getter;

@Getter
public class CartAddRequest {
    private Long menuId;
    private Long quantity;
}
