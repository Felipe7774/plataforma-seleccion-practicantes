CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT ck_users_role CHECK (role IN ('ANALYST', 'ADMIN'))
);

CREATE TABLE applicants (
    id UUID PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    email VARCHAR(254) NOT NULL,
    career VARCHAR(200) NOT NULL,
    semester SMALLINT NOT NULL,
    cv_key VARCHAR(255) NOT NULL,
    cv_original_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_applicants_email UNIQUE (email),
    CONSTRAINT ck_applicants_semester CHECK (semester BETWEEN 1 AND 12),
    CONSTRAINT ck_applicants_status CHECK (status IN ('PENDING', 'VIABLE', 'NOT_VIABLE'))
);

CREATE TABLE status_history (
    id UUID PRIMARY KEY,
    applicant_id UUID NOT NULL,
    previous_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    comment VARCHAR(1000),
    changed_by UUID NOT NULL,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_status_history_applicant
        FOREIGN KEY (applicant_id) REFERENCES applicants (id) ON DELETE CASCADE,
    CONSTRAINT fk_status_history_user
        FOREIGN KEY (changed_by) REFERENCES users (id),
    CONSTRAINT ck_status_history_previous_status
        CHECK (previous_status IS NULL OR previous_status IN ('PENDING', 'VIABLE', 'NOT_VIABLE')),
    CONSTRAINT ck_status_history_new_status
        CHECK (new_status IN ('PENDING', 'VIABLE', 'NOT_VIABLE'))
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_applicants_status ON applicants (status);
CREATE INDEX idx_applicants_career ON applicants (career);
CREATE INDEX idx_applicants_created_at ON applicants (created_at DESC);
CREATE INDEX idx_status_history_applicant_id ON status_history (applicant_id, changed_at);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);

