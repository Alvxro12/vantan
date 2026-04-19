package com.vantan.backend.auth.application;

import com.vantan.backend.auth.application.dto.AuthResponse;
import com.vantan.backend.auth.application.dto.LoginRequest;
import com.vantan.backend.shared.exception.BusinessException;
import com.vantan.backend.shared.security.JwtService;
import com.vantan.backend.user.domain.User;
import com.vantan.backend.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse execute(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> BusinessException.badRequest("INVALID_CREDENTIALS", "Invalid email or password"));

        if (!user.isActive()) {
            throw BusinessException.forbidden("ACCOUNT_DISABLED", "Account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw BusinessException.badRequest("INVALID_CREDENTIALS", "Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name()
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(900000)
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole().name())
                        .build())
                .build();
    }
}