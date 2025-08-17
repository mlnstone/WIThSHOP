package com.example.backend.user.dto;

import com.example.backend.common.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "생년월일은 필수입니다.")
    private String birth;

    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^[0-9\\-+]{8,20}$",
            message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;
}