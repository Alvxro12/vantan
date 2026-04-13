package com.vantan.backend.auth.application;

import com.vantan.backend.auth.domain.*;
import com.vantan.backend.shared.security.JwtService;
import com.vantan.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityEventRepository securityEventRepository;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Transactional
    public void execute(String accessToken, String refreshToken,
                        String ipAddress, String userAgent, User user) {

        // Agregar access token a blacklist en Redis con TTL
        long expirationMs = jwtService.extractExpirationMs(accessToken);
        long remainingMs = expirationMs - System.currentTimeMillis();

        if (remainingMs > 0) {
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + accessToken,
                    "revoked",
                    remainingMs,
                    TimeUnit.MILLISECONDS
            );
        }

        // Revocar refresh token en BD
        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
                rt.setRevoked(true);
                rt.setRevokedAt(OffsetDateTime.now());
                refreshTokenRepository.save(rt);
            });
        }

        // Registrar evento
        securityEventRepository.save(SecurityEvent.builder()
                .eventType(SecurityEventType.LOGOUT)
                .severity(SecurityEventSeverity.INFO)
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build());
    }
}
