package site.techmoa.app.common.article

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ArticleViewHandler(
    private val articlePort: ArticlePort
) {
    @Transactional
    fun increaseViews(id: Long) {
        if (articlePort.increaseViews(id) == 0) {
            throw RuntimeException("Failed to increase views for article $id")
        }
    }
}