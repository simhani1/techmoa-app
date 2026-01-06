package site.techmoa.core.article

import site.techmoa.core.blog.Blog

data class ArticleContent(
    val article: Article,
    val blog: Blog
)