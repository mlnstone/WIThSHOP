package com.example.backend.cart.controller;

import com.example.backend.cart.dto.CartAddRequest;
import com.example.backend.cart.dto.CartChangeQtyRequest;
import com.example.backend.cart.dto.CartItemResponse;
import com.example.backend.cart.dto.CartListResponse;
import com.example.backend.cart.entity.Cart;
import com.example.backend.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "장바구니")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "내 장바구니 조회")
    @GetMapping
    public ResponseEntity<CartListResponse> myCart(Principal principal) {
        List<Cart> carts = cartService.myCart(principal);
        return ResponseEntity.ok(CartListResponse.from(carts));
    }

    @Operation(summary = "장바구니 담기(동일 메뉴면 수량만 증가)")
    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> add(
            Principal principal,
            @RequestBody CartAddRequest req
    ) {
        Cart saved = cartService.addItem(principal, req.getMenuId(), req.getQuantity());
        return ResponseEntity.ok(CartItemResponse.from(saved));
    }

    @Operation(summary = "장바구니 수량 변경")
    @PutMapping("/items/{cartId}")
    public ResponseEntity<CartItemResponse> changeQty(
            Principal principal,
            @PathVariable Long cartId,
            @RequestBody CartChangeQtyRequest req
    ) {
        Cart changed = cartService.changeQuantity(principal, cartId, req.getQuantity());
        return ResponseEntity.ok(CartItemResponse.from(changed));
    }

    @Operation(summary = "장바구니 항목 삭제")
    @DeleteMapping("/items/{cartId}")
    public ResponseEntity<String> remove(
            Principal principal,
            @PathVariable Long cartId
    ) {
        cartService.removeItem(principal, cartId);
        return ResponseEntity.ok("삭제되었습니다.");
    }

    @Operation(summary = "장바구니 비우기")
    @DeleteMapping
    public ResponseEntity<String> clear(Principal principal) {
        cartService.clear(principal);
        return ResponseEntity.ok("삭제되었습니다.");
    }
}