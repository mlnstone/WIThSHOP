package com.example.backend.board.dto;

import com.example.backend.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardResponseDto {

    private Long boardId;
    private String boardTitle;
    private String boardContent;
    private Long hit;
    private String boardTypeTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BoardResponseDto from(Board board) {
        return BoardResponseDto.builder()
                .boardId(board.getBoardId())
                .boardTitle(board.getBoardTitle())
                .boardContent(board.getBoardContent())
                .hit(board.getHit())
                .boardTypeTitle(board.getBoardType().getName())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}