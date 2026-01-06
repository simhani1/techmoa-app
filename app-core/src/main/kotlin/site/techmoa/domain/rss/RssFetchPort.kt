package site.techmoa.domain.rss

import site.techmoa.domain.article.Article
import site.techmoa.domain.blog.Blog
import site.techmoa.domain.blog.BlogStatus

interface RssFetchPort {
    fun saveAll(article: List<Article>)
    fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean

    fun findAll(blogStatus: BlogStatus): List<Blog>
    fun findAllBy(ids: List<Long>): List<Blog>
    fun findAllBy(status: BlogStatus): List<Blog>
}