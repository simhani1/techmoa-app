CREATE TABLE blog
(
    blog_id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at       datetime              NOT NULL,
    updated_at       datetime              NOT NULL,
    link             VARCHAR(1000)         NOT NULL,
    logo_url         VARCHAR(1000)         NULL,
    rss_link         VARCHAR(1000)         NOT NULL,
    operation_status VARCHAR(20)           NOT NULL,
    CONSTRAINT pk_blog PRIMARY KEY (blog_id)
);

CREATE TABLE article
(
    article_id BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NOT NULL,
    blog_id    BIGINT                NOT NULL,
    title      VARCHAR(500)          NOT NULL,
    link       VARCHAR(1000)         NOT NULL,
    guid       VARCHAR(1000)         NOT NULL,
    pub_date   datetime              NOT NULL,
    views      INT                   NOT NULL,
    CONSTRAINT pk_article PRIMARY KEY (article_id)
);