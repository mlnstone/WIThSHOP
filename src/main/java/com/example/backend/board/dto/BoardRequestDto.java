package com.example.backend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardRequestDto {

    private String boardTitle;
    private String boardContent;
    private Long boardTypeId;
}
