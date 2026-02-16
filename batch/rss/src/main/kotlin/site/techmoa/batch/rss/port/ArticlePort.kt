package site.techmoa.batch.rss.port

import site.techmoa.batch.rss.domain.model.Article

interface ArticlePort {
    fun saveAllIgnoringDuplicates(articles: List<Article>)
    fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean
}
