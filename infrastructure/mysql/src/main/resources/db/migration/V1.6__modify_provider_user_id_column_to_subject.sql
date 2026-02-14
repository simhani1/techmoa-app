ALTER TABLE member
    DROP INDEX uk_oauth,
    ADD CONSTRAINT uk_provider_subject UNIQUE (provider, subject);
