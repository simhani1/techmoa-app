package site.techmoa.app.article.support

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.article.domain.Article
import site.techmoa.app.core.OffsetLimit
import site.techmoa.app.storage.db.repository.ArticleRepository

@Component
class ArticleFinder(
    private val articleRepository: ArticleRepository
) {
    @Transactional(readOnly = true)
    fun findPublishedAfter(cursor: Long?, offsetLimit: OffsetLimit): List<Article> {
        val contents = articleRepository.findPublishedAfter(cursor, offsetLimit.toPageableWithCursor())
            .map { it ->
                Article(
                    id = it.id,
                    blogId = it.blogId,
                    title = it.title,
                    link = it.link,
                    pubDate = it.pubDate,
                    views = it.getViews()
                )
            }
        return contents
    }
}