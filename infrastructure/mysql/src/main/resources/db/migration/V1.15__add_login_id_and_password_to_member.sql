ALTER TABLE member
    ADD COLUMN login_id VARCHAR(50) NULL AFTER subject,
    ADD COLUMN password VARCHAR(100) NULL AFTER login_id;

ALTER TABLE member
    ADD CONSTRAINT uk_login_id UNIQUE (login_id);
