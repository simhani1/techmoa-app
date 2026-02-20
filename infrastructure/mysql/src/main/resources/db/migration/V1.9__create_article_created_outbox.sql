CREATE TABLE article_created_outbox
(
    article_created_outbox_id BIGINT AUTO_INCREMENT NOT NULL,
    payload                   LONGTEXT              NOT NULL,
    idempotency_key           VARCHAR(150)          NOT NULL,
    status                    VARCHAR(20)           NOT NULL,
    published_at              DATETIME              NULL,
    last_error_message        VARCHAR(1000)         NULL,
    created_at                DATETIME              NOT NULL,
    updated_at                DATETIME              NOT NULL,
    CONSTRAINT pk_article_created_outbox PRIMARY KEY (article_created_outbox_id),
    CONSTRAINT uk_article_created_outbox_idempotency_key UNIQUE (idempotency_key)
);
