DROP TABLE IF EXISTS translations;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE translations (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    field_key VARCHAR(255) NOT NULL,

    source_text TEXT NOT NULL,
    translated_text TEXT,

    source_lang VARCHAR(10) NOT NULL,
    target_lang VARCHAR(10) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),

    UNIQUE(user_id, field_key, target_lang)
);