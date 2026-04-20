package com.vantan.backend.auth.infrastructure;

import com.vantan.backend.auth.application.*;
import com.vantan.backend.auth.application.dto.AuthResponse;
import com.vantan.backend.auth.application.dto.LoginRequest;
import com.vantan.backend.auth.application.dto.RegisterRequest;
import com.vantan.backend.shared.response.ApiResponse;
import com.vantan.backend.user.domain.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 días en segundos

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        AuthResponse auth = registerUseCase.execute(request);
        setRefreshTokenCookie(response, auth.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", sanitize(auth)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse auth = loginUseCase.execute(request, ip, userAgent);
        setRefreshTokenCookie(response, auth.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Login successful", sanitize(auth)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        AuthResponse auth = refreshTokenUseCase.refresh(refreshToken, ip, userAgent);
        setRefreshTokenCookie(response, auth.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", sanitize(auth)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            @AuthenticationPrincipal User user
    ) {
        String accessToken = extractAccessToken(request);
        String refreshToken = extractRefreshTokenFromCookie(request);
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        logoutUseCase.execute(accessToken, refreshToken, ip, userAgent, user);
        clearRefreshTokenCookie(response);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true en producción con HTTPS
        cookie.setPath("/api/auth");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private AuthResponse sanitize(AuthResponse auth) {
        // No enviamos el refresh token en el body — vive en la HttpOnly cookie
        return AuthResponse.builder()
                .accessToken(auth.getAccessToken())
                .tokenType(auth.getTokenType())
                .expiresIn(auth.getExpiresIn())
                .user(auth.getUser())
                .build();
    }
}