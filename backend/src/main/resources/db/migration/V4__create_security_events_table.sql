CREATE TABLE security_events (
                                 id         BIGSERIAL   PRIMARY KEY,
                                 event_type VARCHAR(50) NOT NULL
                                     CHECK (event_type IN (
                                                           'LOGIN_SUCCESS',
                                                           'LOGIN_FAILED',
                                                           'LOGOUT',
                                                           'TOKEN_REUSE_DETECTED',
                                                           'FAMILY_INVALIDATED',
                                                           'RATE_LIMIT_TRIGGERED'
                                         )),
                                 severity   VARCHAR(20) NOT NULL DEFAULT 'INFO'
                                     CHECK (severity IN ('INFO', 'WARN', 'CRITICAL')),
                                 user_id    BIGINT      REFERENCES users(id) ON DELETE SET NULL,
                                 family_id  VARCHAR(36),
                                 ip_address VARCHAR(45),
                                 user_agent TEXT,
                                 metadata   JSONB,
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_security_events_user_id    ON security_events(user_id);
CREATE INDEX idx_security_events_event_type ON security_events(event_type);
CREATE INDEX idx_security_events_severity   ON security_events(severity);
CREATE INDEX idx_security_events_created_at ON security_events(created_at);
CREATE INDEX idx_security_events_family_id  ON security_events(family_id);