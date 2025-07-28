package com.example.backend.auth.dto;

import com.example.backend.common.enums.Gender;
import com.example.backend.common.enums.Role;
import com.example.backend.common.enums.UserProvider;
import com.example.backend.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {

    @NotNull(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotNull(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(
            regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message="비밀번호는 영문자와, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자로 설정해주세요."
    )
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    private String birth;
    private String gender; // "M" or "W"
    private String phone;

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