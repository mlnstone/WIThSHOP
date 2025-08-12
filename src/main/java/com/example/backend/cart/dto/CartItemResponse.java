package com.example.backend.cart.dto;

import com.example.backend.cart.entity.Cart;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponse {
    private Long cartId;
    private String menuName;
    private Long salePrice;
    private Long quantity;
    private Long itemTotal; // salePrice * quantity

    public static CartItemResponse from(Cart cart) {
        return CartItemResponse.builder()
                .cartId(cart.getCartId())
                .menuName(cart.getMenu().getMenuName())
                .salePrice(cart.getMenu().getSalePrice())
                .quantity(cart.getQuantity())
                .itemTotal(cart.getMenu().getSalePrice() * cart.getQuantity())
                .build();
    }
}