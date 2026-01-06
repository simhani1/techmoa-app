package site.techmoa.app.core.blog

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BlogFinder(
    private val blogPort: BlogPort
) {
    @Transactional(readOnly = true)
    fun findAll(ids: List<Long>): List<Blog> {
        return blogPort.findAllBy(ids)
    }
}