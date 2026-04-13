package com.vantan.backend.auth.application;

import com.vantan.backend.auth.application.dto.AuthResponse;
import com.vantan.backend.auth.application.dto.LoginRequest;
import com.vantan.backend.auth.domain.*;
import com.vantan.backend.shared.exception.BusinessException;
import com.vantan.backend.shared.security.JwtService;
import com.vantan.backend.user.domain.User;
import com.vantan.backend.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final SecurityEventRepository securityEventRepository;

    @Transactional
    public AuthResponse execute(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logFailedLogin(null, ipAddress, userAgent, request.getEmail());
                    return BusinessException.badRequest("INVALID_CREDENTIALS", "Invalid email or password");
                });

        if (!user.isActive()) {
            throw BusinessException.forbidden("ACCOUNT_DISABLED", "Account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logFailedLogin(user, ipAddress, userAgent, request.getEmail());
            throw BusinessException.badRequest("INVALID_CREDENTIALS", "Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name()
        );

        String refreshToken= refreshTokenUseCase.createRefreshToken(user);

        logSuccessLogin(user, ipAddress, userAgent);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .refreshToken(refreshToken)
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

    private void logFailedLogin(User user, String ipAddress, String userAgent, String email) {
        securityEventRepository.save(SecurityEvent.builder()
                .eventType(SecurityEventType.LOGIN_FAILED)
                .severity(SecurityEventSeverity.WARN)
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(java.util.Map.of("email", email))
                .build());
    }

    private void logSuccessLogin(User user, String ipAddress, String userAgent) {
        securityEventRepository.save(SecurityEvent.builder()
                .eventType(SecurityEventType.LOGIN_SUCCESS)
                .severity(SecurityEventSeverity.INFO)
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build());
    }
}