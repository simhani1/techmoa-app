package site.techmoa.worker.rss.port

import site.techmoa.worker.rss.domain.model.Article

interface ArticlePort {
    fun saveAllIgnoringDuplicates(articles: List<Article>)
    fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean
}
