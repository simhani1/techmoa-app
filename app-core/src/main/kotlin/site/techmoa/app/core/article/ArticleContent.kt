package site.techmoa.app.core.article

import site.techmoa.app.core.blog.Blog

data class ArticleContent(
    val article: Article,
    val blog: Blog
)