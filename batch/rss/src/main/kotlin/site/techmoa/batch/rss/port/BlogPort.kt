package site.techmoa.batch.rss.port

import site.techmoa.batch.rss.domain.model.Blog
import site.techmoa.batch.rss.domain.model.BlogStatus

interface BlogPort {
    fun findAllBy(active: BlogStatus): List<Blog>
}