package site.techmoa.application.port

import site.techmoa.application.common.OffsetLimit
import site.techmoa.domain.model.Article

interface ArticlePort {
    fun findById(id: Long): Article?
    fun save(article: Article)
    fun findPublishedAfter(cursor: Long?, offsetLimit: OffsetLimit): List<Article>
    fun saveAll(articles: List<Article>)
    fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean
    fun increaseViews(id: Long): Int
}