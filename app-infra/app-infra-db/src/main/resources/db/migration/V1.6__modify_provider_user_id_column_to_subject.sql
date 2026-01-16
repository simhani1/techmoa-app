ALTER TABLE member
    RENAME COLUMN provider_user_id TO subject;

ALTER TABLE member
    DROP INDEX uk_auth,
    ADD CONSTRAINT uk_provider_subject UNIQUE (provider, subject);
