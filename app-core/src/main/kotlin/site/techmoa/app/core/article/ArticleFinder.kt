package site.techmoa.app.core.article

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.core.common.OffsetLimit

@Component
class ArticleFinder(
    private val articlePort: ArticlePort
) {
    @Transactional(readOnly = true)
    fun findPublishedAfter(cursor: Long?, offsetLimit: OffsetLimit): List<Article> {
        return articlePort.findPublishedAfter(cursor, offsetLimit)
    }
}