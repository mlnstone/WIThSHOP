package com.example.backend.board.dto;

import com.example.backend.board.entity.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardTypeResponseDto {

    private Long boardTypeId;
    private String boardTypeName;

    public static BoardTypeResponseDto from(BoardType boardType) {
        return new BoardTypeResponseDto(boardType.getBoardTypeId(), boardType.getName());
    }
}
