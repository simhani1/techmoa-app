package site.techmoa.app.common.article.port

import site.techmoa.app.common.article.domain.Article
import site.techmoa.app.common.common.OffsetLimit

interface ArticlePort {
    fun findById(id: Long): Article?
    fun save(article: Article)
    fun findPublishedAfter(cursor: Long?, offsetLimit: OffsetLimit): List<Article>
    fun saveAll(articles: List<Article>)
    fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean
    fun increaseViews(id: Long): Int
}