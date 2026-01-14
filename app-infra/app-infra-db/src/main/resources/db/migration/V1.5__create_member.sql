CREATE TABLE member
(
    member_id        BIGINT AUTO_INCREMENT NOT NULL,
    created_at       datetime              NOT NULL,
    updated_at       datetime              NOT NULL,
    email            VARCHAR(64)           NOT NULL,
    provider         VARCHAR(20)           NOT NULL,
    provider_user_id VARCHAR(64)           NOT NULL,
    CONSTRAINT pk_member PRIMARY KEY (member_id)
);

ALTER TABLE member
    ADD CONSTRAINT uk_oauth UNIQUE (provider, provider_user_id);