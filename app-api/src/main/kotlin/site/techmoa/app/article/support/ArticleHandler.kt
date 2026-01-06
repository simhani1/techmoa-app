package site.techmoa.app.article.support

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.storage.db.repository.ArticleRepository

@Component
class ArticleHandler(
    private val articleRepository: ArticleRepository
) {
    @Transactional
    fun increaseViewCount(articleId: Long) {
        val article = articleRepository.findByIdOrNull(articleId)
            ?: throw RuntimeException("Article with id $articleId not found")
        article.increaseViews()
    }
}