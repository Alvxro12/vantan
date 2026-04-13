package com.vantan.backend.auth.application;

import com.vantan.backend.auth.application.dto.AuthResponse;
import com.vantan.backend.auth.domain.*;
import com.vantan.backend.shared.exception.BusinessException;
import com.vantan.backend.shared.security.JwtService;
import com.vantan.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityEventRepository securityEventRepository;
    private final JwtService jwtService;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Transactional
    public String createRefreshToken(User user) {
        return createRefreshToken(user, UUID.randomUUID().toString());
    }

    @Transactional
    public String createRefreshToken(User user, String familyId) {
        String token = jwtService.generateOpaqueToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .familyId(familyId)
                .expiresAt(OffsetDateTime.now().plusNanos(refreshExpirationMs * 1_000_000))
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Transactional
    public String rotate(String oldTokenValue, String ipAddress, String userAgent) {
        RefreshToken oldToken = refreshTokenRepository.findByToken(oldTokenValue)
                .orElseThrow(() -> BusinessException.badRequest(
                        "INVALID_REFRESH_TOKEN", "Refresh token not found"));

        if (oldToken.isRevoked()) {
            // Reuse detectado — invalidar toda la familia
            refreshTokenRepository.revokeAllByFamilyId(oldToken.getFamilyId());
            logEvent(SecurityEventType.TOKEN_REUSE_DETECTED, SecurityEventSeverity.CRITICAL,
                    oldToken.getUser(), oldToken.getFamilyId(), ipAddress, userAgent);
            logEvent(SecurityEventType.FAMILY_INVALIDATED, SecurityEventSeverity.CRITICAL,
                    oldToken.getUser(), oldToken.getFamilyId(), ipAddress, userAgent);
            throw BusinessException.badRequest("SESSION_COMPROMISED",
                    "Session compromised, please login again");
        }

        if (oldToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw BusinessException.badRequest("REFRESH_TOKEN_EXPIRED", "Refresh token expired");
        }

        // Revocar token viejo
        String newTokenValue = jwtService.generateOpaqueToken();
        oldToken.setRevoked(true);
        oldToken.setRevokedAt(OffsetDateTime.now());
        oldToken.setReplacedBy(newTokenValue);
        refreshTokenRepository.save(oldToken);

        // Crear nuevo token con misma familia
        createRefreshToken(oldToken.getUser(), oldToken.getFamilyId());

        return newTokenValue;
    }

    private void logEvent(SecurityEventType type, SecurityEventSeverity severity,
                          User user, String familyId, String ip, String ua) {
        securityEventRepository.save(SecurityEvent.builder()
                .eventType(type)
                .severity(severity)
                .user(user)
                .familyId(familyId)
                .ipAddress(ip)
                .userAgent(ua)
                .build());
    }

    public AuthResponse refresh(String refreshToken, String ipAddress, String userAgent) {
        String newRefreshToken = rotate(refreshToken, ipAddress, userAgent);

        RefreshToken rt = refreshTokenRepository.findByToken(newRefreshToken)
                .orElseThrow(() -> BusinessException.badRequest(
                        "INVALID_REFRESH_TOKEN", "Refresh token not found"));

        User user = rt.getUser();
        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name()
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
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