package site.techmoa.app.blog.support

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.blog.domain.Blog
import site.techmoa.app.storage.db.repository.BlogRepository

@Component
class BlogFinder(
    private val blogRepository: BlogRepository
) {
    @Transactional(readOnly = true)
    fun findById(id: Long): Blog {
        val blogEntity = blogRepository.findByIdOrNull(id) ?: throw RuntimeException("Blog with id $id not found")
        return Blog(id = blogEntity.id, name = blogEntity.link)
    }
}