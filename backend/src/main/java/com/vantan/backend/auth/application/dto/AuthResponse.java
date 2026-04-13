package com.vantan.backend.auth.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private long expiresIn;
    private UserDto user;

    @Getter
    @Builder
    public static class UserDto {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
    }
}