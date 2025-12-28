ALTER TABLE article
    ADD CONSTRAINT uk_article_blog_guid UNIQUE (blog_id, guid);