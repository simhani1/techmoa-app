package site.techmoa.app.storage.db.adapter

import org.springframework.stereotype.Repository
import site.techmoa.app.core.blog.Blog
import site.techmoa.app.core.blog.BlogPort
import site.techmoa.app.core.blog.BlogStatus
import site.techmoa.app.storage.db.entity.BlogEntity
import site.techmoa.app.storage.db.repository.BlogJpaRepository

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