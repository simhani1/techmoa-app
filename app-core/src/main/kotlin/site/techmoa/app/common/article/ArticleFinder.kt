package site.techmoa.app.common.article

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.common.common.OffsetLimit

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