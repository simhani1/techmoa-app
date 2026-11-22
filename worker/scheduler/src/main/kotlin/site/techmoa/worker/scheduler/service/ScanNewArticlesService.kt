package site.techmoa.worker.scheduler.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.techmoa.domain.event.ArticleNotification
import site.techmoa.worker.scheduler.repository.ArticleRepository
import site.techmoa.worker.scheduler.repository.OutboxRepository
import site.techmoa.worker.scheduler.repository.WebhookRepository
import site.techmoa.worker.scheduler.usecase.LastScannedArticleUseCase

@Service
class ScanNewArticlesService(
    private val lastScannedArticleUseCase: LastScannedArticleUseCase,
    private val outboxRepository: OutboxRepository,
    private val articleRepository: ArticleRepository,
    private val webhookRepository: WebhookRepository,
) {
    @Transactional
    fun execute() {
        val lastScanned = lastScannedArticleUseCase.getLastScannedArticle()
        val newArticles = articleRepository.findByIdGreaterThan(lastScanned.articleId)
        if (newArticles.isEmpty()) return

        for (webhook in webhookRepository.getValidWebhook()) {
            val notifications = mutableListOf<ArticleNotification>()
            for (article in newArticles) {
                if (article.storedAt <= webhook.registeredAt) continue
                notifications.add(ArticleNotification(
                    articleId = article.articleId,
                    title = article.title,
                    link = article.link,
                    pubDate = article.pubDate,
                    blogName = article.blogName,
                    webhookUrl = webhook.url,
                ))
            }
            if (notifications.isNotEmpty()) outboxRepository.enqueue(webhook.id, notifications)
        }
        lastScannedArticleUseCase.sync(newArticles.last().articleId)
    }
}
