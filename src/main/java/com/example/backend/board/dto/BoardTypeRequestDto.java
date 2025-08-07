package com.example.backend.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardTypeRequestDto {

    @NotBlank(message = "게시판 타입 이름은 필수입니다.")
    private String name;
}
