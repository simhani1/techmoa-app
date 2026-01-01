package site.techmoa.app.article.domain

import site.techmoa.app.blog.domain.Blog

data class ArticleContent(
    val article: Article,
    val blog: Blog
)