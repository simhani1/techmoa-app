package site.techmoa.application.dto

import site.techmoa.domain.model.Article
import site.techmoa.domain.model.Blog

data class ArticleDto(
    val article: Article,
    val blog: Blog
) {
}