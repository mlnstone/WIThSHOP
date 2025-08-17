package com.example.backend.auth.dto;

import com.example.backend.common.enums.Gender;
import com.example.backend.common.enums.UserProvider;
import com.example.backend.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private String userEmail;
    private String userName;
    private String birth;
    private String gender;          // M → 남성, W → 여성
    private String phone;
    private LocalDateTime userCreatedAt;
    private UserProvider userProvider;

    public static UserProfileDto from(User user) {
        String genderStr = null;
        if (user.getGender() == Gender.M) genderStr = "남성";
        else if (user.getGender() == Gender.W) genderStr = "여성";

        return new UserProfileDto(
                user.getUserEmail(),
                user.getUserName(),
                user.getBirth(),
                genderStr,
                user.getPhone(),
                user.getCreatedAt(),
                user.getUserProvider()
        );
    }
}