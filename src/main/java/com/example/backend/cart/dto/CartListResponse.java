package com.example.backend.cart.dto;

import com.example.backend.cart.entity.Cart;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CartListResponse {
    private List<CartItemResponse> items;
    private Long totalPrice; // 모든 itemTotal 합계
    private Long totalCount; // 장바구니 총 개수

    public static CartListResponse from(List<Cart> carts) {
        List<CartItemResponse> itemResponses = carts.stream()
                .map(CartItemResponse::from)
                .toList();

        long total = itemResponses.stream()
                .mapToLong(CartItemResponse::getItemTotal)
                .sum();

        return CartListResponse.builder()
                .items(itemResponses)
                .totalPrice(total)
                .totalCount((long) itemResponses.size())
                .build();
    }
}