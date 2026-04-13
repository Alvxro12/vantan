package com.vantan.backend.auth.infrastructure;

import com.vantan.backend.auth.domain.SecurityEvent;
import com.vantan.backend.auth.domain.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SecurityEventRepositoryImpl implements SecurityEventRepository {

    private final SecurityEventJpaRepository jpa;

    @Override
    public SecurityEvent save(SecurityEvent event) {
        return jpa.save(event);
    }
}