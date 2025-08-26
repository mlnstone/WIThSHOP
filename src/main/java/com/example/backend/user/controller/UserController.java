package com.example.backend.user.controller;

import com.example.backend.auth.dto.UserProfileDto;
import com.example.backend.user.dto.ChangePasswordRequest;
import com.example.backend.user.dto.UpdateProfileRequest;
import com.example.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "유저", description = "유저")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserProfileDto myProfile(Principal principal) {
        String email = principal.getName();
        return userService.getMyProfile(email);
    }

    @PostMapping("/me/password")
    public void changePassword(Principal principal, @RequestBody @Valid ChangePasswordRequest req) {
        userService.changePassword(principal.getName(), req.getCurrentPw(), req.getNewPw());
    }

    @PutMapping("/me/profile")
    public UserProfileDto updateOauth2Profile(Principal principal, @RequestBody @Valid UpdateProfileRequest req) {
        return userService.updateOauth2Profile(principal.getName(), req);
    }
}