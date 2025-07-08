package com.example.backend.auth.dto;

import com.example.backend.common.enums.Gender;
import com.example.backend.common.enums.Role;
import com.example.backend.common.enums.UserProvider;
import com.example.backend.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
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