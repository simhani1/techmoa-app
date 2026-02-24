package site.techmoa.application.service

import org.springframework.stereotype.Service
import site.techmoa.application.port.BlogPort
import site.techmoa.domain.model.Blog
import site.techmoa.domain.model.BlogStatus

@Service
class BlogService(
    private val blogPort: BlogPort
) {

    fun getAllBlogs(): List<Blog> {
        return blogPort.findAllBy(BlogStatus.ACTIVE)
    }
}