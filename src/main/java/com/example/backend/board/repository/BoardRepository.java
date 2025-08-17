package com.example.backend.board.repository;

import com.example.backend.board.entity.Board;
import com.example.backend.board.entity.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByBoardTitleContaining(String keyword, Pageable pageable);

    boolean existsByBoardType(BoardType boardType);

    List<Board> findAllByBoardType(BoardType boardType);

    Page<Board> findByBoardType_BoardTypeIdAndBoardTitleContaining(Long typeId, String search, Pageable pageable);

    Page<Board> findByBoardType_BoardTypeId(Long typeId, Pageable pageable);
}