package site.techmoa.app.core.article

import site.techmoa.app.core.common.OffsetLimit

interface ArticlePort {
    fun findById(id: Long): Article?
    fun save(article: Article)
    fun findPublishedAfter(cursor: Long?, offsetLimit: OffsetLimit): List<Article>
    fun saveAll(article: List<Article>)
    fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean
}