package site.techmoa.infrastructure.jpa.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import site.techmoa.application.port.BlogPort
import site.techmoa.domain.model.Blog
import site.techmoa.domain.model.BlogStatus
import site.techmoa.infrastructure.jpa.entity.BlogEntity
import site.techmoa.infrastructure.jpa.repository.BlogJpaRepository

@Repository
class BlogAdapter(
    private val blogRepository: BlogJpaRepository
) : BlogPort {
    override fun save(blog: Blog) {
        val entity = BlogEntity.of(
            name = blog.name,
            link = blog.link,
            logoUrl = blog.logoUrl,
            rssLink = blog.rssLink,
        )
        blogRepository.save(entity)
    }

    override fun findById(blogId: Long): Blog? {
        val entity = blogRepository.findByIdOrNull(blogId)
        return entity?.let {
            Blog(
                id = it.id,
                name = it.name,
                link = it.link,
                logoUrl = it.logoUrl,
                rssLink = it.rssLink,
            )
        }
    }

    override fun findAllBy(ids: List<Long>): List<Blog> {
        val entities = blogRepository.findAllById(ids)
        return entities.map {
            Blog(
                id = it.id,
                name = it.name,
                link = it.link,
                logoUrl = it.logoUrl,
                rssLink = it.rssLink,
            )
        }
    }

    override fun findAllBy(status: BlogStatus): List<Blog> {
        val entities = blogRepository.findAllStatus(status)
        return entities.map {
            Blog(
                id = it.id,
                name = it.name,
                link = it.link,
                logoUrl = it.logoUrl,
                rssLink = it.rssLink,
            )
        }
    }
}