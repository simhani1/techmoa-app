CREATE TABLE last_scanned_article_id
(
    job_name                VARCHAR(100) NOT NULL,
    last_scanned_article_id BIGINT       NOT NULL,
    created_at              DATETIME     NOT NULL,
    updated_at              DATETIME     NOT NULL,
    CONSTRAINT pk_last_scanned_article_id PRIMARY KEY (job_name)
);
