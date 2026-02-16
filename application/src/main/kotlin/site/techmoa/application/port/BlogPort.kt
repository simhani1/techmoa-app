package site.techmoa.application.port

import site.techmoa.domain.model.Blog
import site.techmoa.domain.model.BlogStatus

interface BlogPort {
    fun save(blog: Blog)
    fun findById(blogId: Long): Blog?
    fun findAllBy(ids: List<Long>): List<Blog>
    fun findAllBy(status: BlogStatus): List<Blog>
}