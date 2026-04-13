package com.vantan.backend.auth.domain;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByFamilyId(String familyId);
    RefreshToken save(RefreshToken refreshToken);
    void saveAll(List<RefreshToken> tokens);
    void revokeAllByFamilyId(String familyId);
}