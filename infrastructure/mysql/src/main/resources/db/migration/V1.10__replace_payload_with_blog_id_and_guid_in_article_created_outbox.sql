ALTER TABLE article_created_outbox
    ADD COLUMN blog_id BIGINT NOT NULL DEFAULT 0 AFTER article_created_outbox_id,
    ADD COLUMN guid VARCHAR(600) NOT NULL DEFAULT '' AFTER blog_id;

ALTER TABLE article_created_outbox
    DROP COLUMN payload;

CREATE INDEX idx_article_created_outbox_blog_id_guid
    ON article_created_outbox (blog_id, guid);
