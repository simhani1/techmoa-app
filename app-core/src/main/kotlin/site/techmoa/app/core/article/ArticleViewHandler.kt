package site.techmoa.app.core.article

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ArticleViewHandler(
    private val articlePort: ArticlePort
) {
    @Transactional
    fun increaseViews(articleId: Long) {
        val article = articlePort.findById(articleId)
            ?: throw RuntimeException("Article with id $articleId not found")
        article.increaseViews()
        articlePort.save(article)
    }
}