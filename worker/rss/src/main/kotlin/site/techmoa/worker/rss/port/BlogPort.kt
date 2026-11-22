package site.techmoa.worker.rss.port

import site.techmoa.worker.rss.domain.model.Blog
import site.techmoa.worker.rss.domain.model.BlogStatus

interface BlogPort {
    fun findAllBy(active: BlogStatus): List<Blog>
}