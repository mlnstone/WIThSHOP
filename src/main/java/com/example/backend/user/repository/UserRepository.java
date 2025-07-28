package com.example.backend.user.repository;

import com.example.backend.common.enums.UserProvider;
import com.example.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String email);

    boolean existsByUserEmail(String email);

    Optional<User> findByUserEmailAndUserProvider(String email, UserProvider provider);
}