CREATE TABLE refresh_tokens (
                                id           BIGSERIAL    PRIMARY KEY,
                                token        VARCHAR(512) NOT NULL UNIQUE,
                                user_id      BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                family_id    VARCHAR(36)  NOT NULL,
                                expires_at   TIMESTAMPTZ  NOT NULL,
                                revoked      BOOLEAN      NOT NULL DEFAULT FALSE,
                                revoked_at   TIMESTAMPTZ,
                                replaced_by  VARCHAR(512),
                                created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_token     ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id   ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_family_id ON refresh_tokens(family_id);