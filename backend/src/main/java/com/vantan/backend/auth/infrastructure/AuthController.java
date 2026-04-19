package com.vantan.backend.auth.infrastructure;

import com.vantan.backend.auth.application.*;
import com.vantan.backend.auth.application.dto.AuthResponse;
import com.vantan.backend.auth.application.dto.LoginRequest;
import com.vantan.backend.auth.application.dto.RegisterRequest;
import com.vantan.backend.shared.response.ApiResponse;
import com.vantan.backend.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
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
    private final LogoutUseCase logoutUseCase;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse auth = registerUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", auth));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        AuthResponse auth = loginUseCase.execute(request, ip, userAgent);
        return ResponseEntity.ok(ApiResponse.success("Login successful", auth));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            @AuthenticationPrincipal User user
    ) {
        String accessToken = extractAccessToken(request);
        logoutUseCase.execute(accessToken, null, null, null, user);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}