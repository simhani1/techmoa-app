CREATE TABLE bookmarked_article
(
    created_at    datetime NOT NULL,
    updated_at    datetime NOT NULL,
    bookmarked_at BIGINT   NOT NULL,
    member_id     BIGINT   NOT NULL,
    article_id    BIGINT   NOT NULL,
    CONSTRAINT pk_bookmarked_article PRIMARY KEY (member_id, article_id)
);