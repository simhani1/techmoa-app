package site.techmoa.application.usecase

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.application.common.OffsetLimit
import site.techmoa.application.common.Page
import site.techmoa.application.port.ArticlePort
import site.techmoa.domain.exception.NotFoundException
import site.techmoa.domain.model.Article

@Component
class GetArticleUseCase(
    private val articlePort: ArticlePort
) {

    @Transactional(readOnly = true)
    fun fetchOne(articleId: Long): Article {
        return articlePort.findById(articleId)
            ?: throw NotFoundException("Article not found with id: $articleId")
    }

    @Transactional(readOnly = true)
    fun fetchPage(cursor: Long?, limit: Int): Page<Article> {
        val fetched = articlePort.findPublishedAfter(cursor, OffsetLimit(limit = limit))
        val articles = fetched.take(limit)

        val hasNext = fetched.size > limit
        val nextCursor = if (hasNext) articles.lastOrNull()?.pubDate else null

        return Page(
            data = articles,
            hasNext = hasNext,
            nextCursor = nextCursor
        )
    }
}
