package com.example.backend.auth.dto;

import com.example.backend.common.enums.Gender;
import com.example.backend.common.enums.Role;
import com.example.backend.common.enums.UserProvider;
import com.example.backend.user.entity.User;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {

    @NotNull(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotNull(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하여야 합니다.")
    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).*",
            message = "비밀번호는 영문자, 숫자, 특수기호가 각 1개 이상 포함되어야 합니다."
    )
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "생년월일은 필수입니다.")
    private String birth;

    @NotNull(message = "성별은 필수입니다.")
    private String gender; // enum M/W

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @NotBlank(message = "인증번호는 필수입니다.")
    private String code;   // 📌 OTP 인증번호 추가

    /**
     * User 엔티티 변환
     */
    public User toEntity(String encodedPassword, Role role, UserProvider provider) {
        return User.builder()
                .userEmail(email)
                .userPwd(encodedPassword)
                .userName(name)
                .birth(birth)
                .gender(Gender.valueOf(gender)) // "M" or "W"
                .phone(phone)
                .userType(role)
                .userProvider(provider)
                .build();
    }
}