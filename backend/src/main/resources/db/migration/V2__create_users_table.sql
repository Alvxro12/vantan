CREATE TABLE users (
                       id         BIGSERIAL    PRIMARY KEY,
                       email      VARCHAR(255) NOT NULL UNIQUE,
                       password   VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name  VARCHAR(100) NOT NULL,
                       role       VARCHAR(20)  NOT NULL DEFAULT 'USER'
                           CHECK (role IN ('USER', 'ADMIN')),
                       active     BOOLEAN      NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);