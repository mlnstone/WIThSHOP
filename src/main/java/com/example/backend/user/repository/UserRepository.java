package com.example.backend.user.repository;

import com.example.backend.common.enums.UserProvider;
import com.example.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String email);

    boolean existsByUserEmail(String email);

    Optional<User> findByUserEmailAndUserProvider(String email, UserProvider provider);

    @Query("select u.userId from User u where u.userEmail = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);
}