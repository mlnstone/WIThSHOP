package com.example.backend.board.entity;

import com.example.backend.board.dto.BoardRequestDto;
import com.example.backend.common.base.EntityDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "board")
public class Board extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    private String boardTitle;

    @Lob
    @Column(nullable = false)
    private String boardContent;

    private Long hit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_type_id", nullable = false)
    private BoardType boardType;

    public void updateBoard(BoardRequestDto boardRequestDto, BoardType boardType) {
        this.boardTitle = boardRequestDto.getBoardTitle();
        this.boardContent = boardRequestDto.getBoardContent();
        this.boardType = boardType;
    }

    public void increaseHit() {
        if (hit == null) hit = 1L;
        else hit++;
    }
}