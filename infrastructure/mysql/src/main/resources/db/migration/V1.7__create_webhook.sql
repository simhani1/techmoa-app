CREATE TABLE webhook
(
    webhook_id  BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NOT NULL,
    updated_at  datetime              NOT NULL,
    member_id   BIGINT                NOT NULL,
    url         VARCHAR(1000)         NOT NULL,
    validity    VARCHAR(20)           NOT NULL,
    platform    VARCHAR(20)           NOT NULL,
    CONSTRAINT pk_webhook PRIMARY KEY (webhook_id),
    CONSTRAINT fk_webhook_member FOREIGN KEY (member_id) REFERENCES member (member_id)
);
