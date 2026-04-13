package com.vantan.backend.auth.infrastructure;

import com.vantan.backend.auth.domain.RefreshToken;
import com.vantan.backend.auth.domain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpa;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpa.findByToken(token);
    }

    @Override
    public List<RefreshToken> findByFamilyId(String familyId) {
        return jpa.findByFamilyId(familyId);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return jpa.save(refreshToken);
    }

    @Override
    @Transactional
    public void revokeAllByFamilyId(String familyId) {
        jpa.revokeAllByFamilyId(familyId);
    }

    @Override
    public void saveAll(List<RefreshToken> tokens) {
        jpa.saveAll(tokens);
    }
}