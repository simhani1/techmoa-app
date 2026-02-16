package site.techmoa.infrastructure.jpa.adapter

import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.techmoa.application.common.OffsetLimit
import site.techmoa.application.port.ArticlePort
import site.techmoa.domain.model.Article
import site.techmoa.infrastructure.jpa.entity.ArticleEntity
import site.techmoa.infrastructure.jpa.repository.ArticleJpaRepository

@Repository
class ArticleAdapter(
    private val articleRepository: ArticleJpaRepository,
) : ArticlePort {

    @Transactional(readOnly = true)
    override fun findById(id: Long): Article? {
        val entity = articleRepository.findByIdOrNull(id)
        return entity?.let {
            Article(
                id = it.id,
                blogId = it.blogId,
                title = it.title,
                link = it.link,
                guid = it.guid,
                pubDate = it.pubDate,
                views = it.getViews()
            )
        }
    }

    @Transactional
    override fun save(article: Article) {
        val entity = ArticleEntity.from(article)
        articleRepository.save(entity)
    }

    @Transactional(readOnly = true)
    override fun findPublishedAfter(
        cursor: Long?,
        offsetLimit: OffsetLimit
    ): List<Article> {
        val pageable = PageRequest.of(
            offsetLimit.offset / offsetLimit.limit,
            offsetLimit.limit + 1
        )
        val entities = articleRepository.findPublishedAfter(
            cursor = cursor,
            pageable = pageable
        )
        return entities.map {
            Article(
                id = it.id,
                blogId = it.blogId,
                title = it.title,
                link = it.link,
                guid = it.guid,
                pubDate = it.pubDate,
                views = it.getViews()
            )
        }
    }

    @Transactional
    override fun saveAll(articles: List<Article>) {
        val entities = articles.map { ArticleEntity.from(it) }
        articleRepository.saveAll(entities)
    }

    @Transactional(readOnly = true)
    override fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean {
        return articleRepository.existsByBlogIdAndGuid(blogId, guid)
    }

    @Transactional
    override fun increaseViews(id: Long): Int {
        return articleRepository.increaseViews(id)
    }

}