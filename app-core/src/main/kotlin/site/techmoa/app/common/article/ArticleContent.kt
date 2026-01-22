package site.techmoa.app.common.article

import site.techmoa.app.common.blog.Blog

data class ArticleContent(
    val article: Article,
    val blog: Blog
)