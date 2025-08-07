package com.example.backend.board.repository;

import com.example.backend.board.entity.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardTypeRepository extends JpaRepository<BoardType, Long> {
    boolean existsByName(String name);
}
