package site.techmoa.app.storage.db.adapter

import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import site.techmoa.app.core.article.domain.Article
import site.techmoa.app.core.article.port.ArticlePort
import site.techmoa.app.core.common.OffsetLimit
import site.techmoa.app.storage.db.entity.ArticleEntity
import site.techmoa.app.storage.db.repository.ArticleJpaRepository

@Repository
class ArticleAdapter(
    private val articleRepository: ArticleJpaRepository,
) : ArticlePort {
    override fun findById(id: Long): Article? {
        val entity = articleRepository.findByIdOrNull(id) ?: return null
        return Article(
            id = entity.id,
            blogId = entity.blogId,
            title = entity.title,
            link = entity.link,
            guid = entity.guid,
            pubDate = entity.pubDate,
            views = entity.getViews()
        )
    }

    override fun save(article: Article) {
        val entity = ArticleEntity.of(
            blogId = article.blogId,
            title = article.title,
            link = article.link,
            guid = article.guid,
            pubDate = article.pubDate
        )
        articleRepository.save(entity)
    }

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

    override fun saveAll(articles: List<Article>) {
        val entities = articles.map {
            ArticleEntity.of(
                blogId = it.blogId,
                title = it.title,
                link = it.link,
                guid = it.guid,
                pubDate = it.pubDate,
            )
        }
        articleRepository.saveAll(entities)
    }

    override fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean {
        return articleRepository.existsByBlogIdAndGuid(blogId, guid)
    }

    override fun increaseViews(id: Long): Int {
        return articleRepository.increaseViews(id)
    }

}