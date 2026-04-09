-- Migración inicial: tabla de control interno
-- Esta tabla documenta que Flyway está activo y el schema está versionado.
-- Las tablas de dominio se crean en sus respectivas fases.

CREATE TABLE IF NOT EXISTS schema_info (
                                           id         SERIAL PRIMARY KEY,
                                           version    VARCHAR(20)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
    );

INSERT INTO schema_info (version, description)
VALUES ('1.0.0', 'Initial schema setup - Vantan e-commerce');
