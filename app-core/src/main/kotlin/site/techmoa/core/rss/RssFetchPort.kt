package site.techmoa.core.rss

import site.techmoa.core.article.Article
import site.techmoa.core.blog.Blog
import site.techmoa.core.blog.BlogStatus

interface RssFetchPort {
    fun saveAll(article: List<Article>)
    fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean

    fun findAllBy(ids: List<Long>): List<Blog>
    fun findAllBy(status: BlogStatus): List<Blog>
}