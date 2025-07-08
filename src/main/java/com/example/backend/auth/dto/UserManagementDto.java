package com.example.backend.auth.dto;


import com.example.backend.common.enums.Gender;
import com.example.backend.common.enums.Role;
import com.example.backend.common.enums.UserProvider;
import com.example.backend.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementDto {

    private Long userId;
    private String userEmail;
    private String userName;
    private String birth;
    private Gender gender;
    private String phone;
    private Role userType;
    private UserProvider userProvider;
    private LocalDateTime userCreatedAt;

    public static UserManagementDto from(User user) {
        return new UserManagementDto(
                user.getUserId(),
                user.getUserEmail(),
                user.getUserName(),
                user.getBirth(),
                user.getGender(),
                user.getPhone(),
                user.getUserType(),
                user.getUserProvider(),
                user.getCreatedAt()
        );
    }
}