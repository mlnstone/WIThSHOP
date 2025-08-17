package com.example.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPw;

    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).*",
            message = "비밀번호는 영문자, 숫자, 특수기호가 각 1개 이상 포함되어야 합니다."
    )
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하여야 합니다.")
    private String newPw;
}