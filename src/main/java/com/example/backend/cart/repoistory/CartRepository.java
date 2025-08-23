package com.example.backend.cart.repoistory;

import com.example.backend.cart.entity.Cart;
import com.example.backend.menu.entity.Menu;
import com.example.backend.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"menu"})
    List<Cart> findByUser(User user);

    Optional<Cart> findByUserAndMenu(User user, Menu menu);

    @Modifying
    @Query("delete from Cart c where c.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    long countByUser_UserId(Long userId);
}