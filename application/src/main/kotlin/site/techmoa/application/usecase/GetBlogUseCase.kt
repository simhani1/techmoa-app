package site.techmoa.application.usecase

import org.springframework.stereotype.Component
import site.techmoa.application.port.BlogPort
import site.techmoa.domain.exception.NotFoundException
import site.techmoa.domain.model.Blog

@Component
class GetBlogUseCase(
    private val blogPort: BlogPort
) {

    fun fetchOne(blogId: Long): Blog {
        val blog = blogPort.findById(blogId)
            ?: throw NotFoundException("Blog not found with id: $blogId")
        return blog
    }

    fun fetchBlogMapByIds(blogIds: List<Long>): Map<Long, Blog> {
        return blogPort.findAllBy(blogIds).associateBy { it.id }
    }
}
