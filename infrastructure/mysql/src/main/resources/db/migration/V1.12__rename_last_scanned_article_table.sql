RENAME TABLE last_scanned_article_id TO last_scanned_article;

ALTER TABLE last_scanned_article
    CHANGE COLUMN last_scanned_article_id last_scanned_id BIGINT NOT NULL;
