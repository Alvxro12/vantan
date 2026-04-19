package com.vantan.backend.auth.application;

import com.vantan.backend.auth.application.dto.AuthResponse;
import com.vantan.backend.auth.application.dto.RegisterRequest;
import com.vantan.backend.shared.exception.BusinessException;
import com.vantan.backend.shared.security.JwtService;
import com.vantan.backend.user.domain.Role;
import com.vantan.backend.user.domain.User;
import com.vantan.backend.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse execute(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw BusinessException.conflict("EMAIL_ALREADY_EXISTS",
                    "Email " + request.getEmail() + " is already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(
                saved.getId(), saved.getEmail(), saved.getRole().name()
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(900000)
                .user(AuthResponse.UserDto.builder()
                        .id(saved.getId())
                        .email(saved.getEmail())
                        .firstName(saved.getFirstName())
                        .lastName(saved.getLastName())
                        .role(saved.getRole().name())
                        .build())
                .build();
    }
}