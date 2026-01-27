package site.techmoa.app.core.article.support

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.core.article.domain.Article
import site.techmoa.app.core.article.port.ArticlePort
import site.techmoa.app.core.common.OffsetLimit

@Component
class ArticleFinder(
    private val articlePort: ArticlePort
) {
    @Transactional(readOnly = true)
    fun findPublishedAfter(cursor: Long?, offsetLimit: OffsetLimit): List<Article> {
        return articlePort.findPublishedAfter(cursor, offsetLimit)
    }

    @Transactional(readOnly = true)
    fun findByIdOrNull(articleId: Long): Article {
        return articlePort.findById(articleId) ?: throw RuntimeException("Article not found with id: $articleId")
    }
}