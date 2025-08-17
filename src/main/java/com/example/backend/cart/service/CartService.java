package com.example.backend.cart.service;

import com.example.backend.cart.entity.Cart;
import com.example.backend.cart.repoistory.CartRepository;
import com.example.backend.common.enums.MenuStatus;
import com.example.backend.menu.entity.Menu;
import com.example.backend.menu.repository.MenuRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public Cart addItem(Principal principal, Long menuId, Long quantity) {
        User user = getUser(principal);
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        if (menu.getStatus() != MenuStatus.ACTIVE) {
            throw new IllegalArgumentException("판매중이 아닌 상품");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상");
        }

        return cartRepository.findByUserAndMenu(user, menu)
                .map(c -> {
                    c.addQuantity(quantity);
                    return c;
                })
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().user(user).menu(menu).quantity(quantity).build()
                ));
    }

    public List<Cart> myCart(Principal principal) {
        return cartRepository.findByUser(getUser(principal));
    }

    @Transactional
    public Cart changeQuantity(Principal principal, Long cartId, Long quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상");
        }
        Cart cart = getOwnedCart(principal, cartId);
        cart.changeQuantity(quantity);
        return cart;
    }

    @Transactional
    public void removeItem(Principal principal, Long cartId) {
        Cart cart = getOwnedCart(principal, cartId);
        cartRepository.delete(cart);
    }

    @Transactional
    public void clear(Principal principal) {
        Long userId = getUser(principal).getUserId();
        cartRepository.deleteAllByUserId(userId);
    }

    private User getUser(Principal principal) {
        return userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }

    private Cart getOwnedCart(Principal principal, Long cartId) {
        User user = getUser(principal);
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목 없음"));
        if (!cart.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("본인 장바구니가 아님");
        }
        return cart;
    }
}