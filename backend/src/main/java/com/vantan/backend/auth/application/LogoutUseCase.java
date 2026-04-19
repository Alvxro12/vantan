package com.vantan.backend.auth.application;

import com.vantan.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    public void execute(String accessToken, String refreshToken,
                        String ipAddress, String userAgent, User user) {
        // Logout simple — el frontend elimina el token de localStorage
        // Blacklist y revocación se implementan en etapa de hardening
    }
}