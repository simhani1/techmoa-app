package site.techmoa.batch.schedules.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import site.techmoa.batch.schedules.repository.ArticleRepository
import site.techmoa.batch.schedules.repository.OutboxRepository
import site.techmoa.batch.schedules.repository.WebhookRepository
import site.techmoa.domain.event.NewArticlesEvents
import site.techmoa.domain.event.WebhookGatewayPort
import site.techmoa.domain.model.WebhookValidity

@Service
class OutboxDispatchService(
    private val outboxRepository: OutboxRepository,
    private val articleRepository: ArticleRepository,
    private val webhookRepository: WebhookRepository,
    private val webhookGatewayPort: WebhookGatewayPort
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val idempotencyKeyRegex = Regex("""^\d{14}:article:(\d+):webhook:(\d+)$""")

    fun dispatchPending() {
        val pending = outboxRepository.findPending()
        if (pending.isEmpty()) return

        pending.forEach { outbox ->
            val webhookId = parseWebhookId(outbox.idempotencyKey)
            if (webhookId == null) {
                outboxRepository.markFailed(
                    outbox.id,
                    "Invalid idempotency key: ${outbox.idempotencyKey}"
                )
                return@forEach
            }

            val article = articleRepository.findByBlogIdAndGuid(
                outbox.blogId,
                outbox.guid
            )

            if (article == null) {
                outboxRepository.markFailed(
                    outbox.id,
                    "Article not found for blogId=${outbox.blogId}, guid=${outbox.guid}"
                )
                return@forEach
            }

            val webhook = webhookRepository.findById(webhookId)
            if (webhook == null) {
                outboxRepository.markFailed(
                    outbox.id,
                    "Webhook not found. webhookId=$webhookId"
                )
                return@forEach
            }

            if (webhook.validity != WebhookValidity.VALID) {
                outboxRepository.markFailed(
                    outbox.id,
                    "Webhook is not valid. webhookId=$webhookId"
                )
                return@forEach
            }

            val event = NewArticlesEvents.NewArticlesEvent(
                article = article,
                webhook = webhook,
                idempotencyKey = outbox.idempotencyKey
            )

            try {
                webhookGatewayPort.publish(event)
                outboxRepository.markPublished(outbox.id)
            } catch (ex: Exception) {
                log.error(
                    "Failed to publish outbox message. outboxId={}, idempotencyKey={}",
                    outbox.id,
                    outbox.idempotencyKey,
                    ex
                )
                outboxRepository.markFailed(
                    outbox.id,
                    ex.message?.take(1000) ?: "Unknown error"
                )
            }
        }
    }

    private fun parseWebhookId(idempotencyKey: String): Long? {
        return idempotencyKeyRegex.find(idempotencyKey)?.groupValues?.getOrNull(2)?.toLongOrNull()
    }
}
