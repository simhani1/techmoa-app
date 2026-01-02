package site.techmoa.app.blog.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import site.techmoa.app.blog.domain.BlogProfile
import site.techmoa.app.storage.db.entity.BlogEntity
import site.techmoa.app.storage.db.repository.BlogRepository

@Service
class BlogService(
    private val blogRepository: BlogRepository
){
    @Transactional
    fun subscribe(blog: BlogProfile) {
        blogRepository.save(BlogEntity.of(blog.link, blog.name, blog.logoUrl, blog.rssLink))
    }
}