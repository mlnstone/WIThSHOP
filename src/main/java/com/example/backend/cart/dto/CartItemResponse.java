package com.example.backend.cart.dto;

import com.example.backend.cart.entity.Cart;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponse {
    private Long cartId;

    private Long menuId;
    private String menuName;
    private String image;
    private Long originalPrice; // 정가
    private Long salePrice;     // 판매가
    private Long stock;         // 재고 (수량 상한 표시에 필요)

    private Long quantity;      // 장바구니 수량
    private Long itemTotal;     // salePrice * quantity

    public static CartItemResponse from(Cart cart) {
        return CartItemResponse.builder()
                .cartId(cart.getCartId())
                .menuId(cart.getMenu().getMenuId())
                .menuName(cart.getMenu().getMenuName())
                .image(cart.getMenu().getImage())
                .originalPrice(cart.getMenu().getOriginalPrice())
                .salePrice(cart.getMenu().getSalePrice())
                .stock(cart.getMenu().getStock())
                .quantity(cart.getQuantity())
                .itemTotal(cart.getMenu().getSalePrice() * cart.getQuantity())
                .build();
    }
}