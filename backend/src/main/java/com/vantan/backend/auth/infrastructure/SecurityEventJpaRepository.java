package com.vantan.backend.auth.infrastructure;

import com.vantan.backend.auth.domain.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityEventJpaRepository extends JpaRepository<SecurityEvent, Long> {
}