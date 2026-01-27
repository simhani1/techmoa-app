package site.techmoa.app.core.article.domain

import site.techmoa.app.core.blog.Blog

data class ArticleContent(
    val article: Article,
    val blog: Blog
)