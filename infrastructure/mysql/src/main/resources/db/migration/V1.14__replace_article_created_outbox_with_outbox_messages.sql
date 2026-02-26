DROP TABLE IF EXISTS article_created_outbox;

CREATE TABLE outbox_messages
(
    outbox_message_id BIGINT AUTO_INCREMENT NOT NULL,
    event_type        VARCHAR(80)      NOT NULL,
    aggregate_type    VARCHAR(80)      NOT NULL,
    aggregate_id      VARCHAR(100)     NOT NULL,
    idempotency_key   VARCHAR(255)     NOT NULL,
    payload           TEXT             NOT NULL,
    payload_type      VARCHAR(40)      NOT NULL,

    status            VARCHAR(20)      NOT NULL DEFAULT 'PENDING',
    published_at      DATETIME(6)      NULL,

    last_error_message VARCHAR(1000)   NULL,

    created_at        DATETIME(6)      NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at        DATETIME(6)      NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_outbox_messages PRIMARY KEY (outbox_message_id),
    CONSTRAINT chk_outbox_status CHECK (status IN ('PENDING', 'PUBLISHING', 'SUCCESS', 'FAIL'))
);
