package site.techmoa.worker.scheduler.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import site.techmoa.domain.event.OutboxMessages
import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage
import site.techmoa.worker.scheduler.repository.RegisteredWebhook
import site.techmoa.worker.scheduler.dto.NewArticleDto
import java.util.*

@Component
class NewArticlesEventUseCase(
    private val objectMapper: ObjectMapper,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun publish(newArticles: List<NewArticleDto>, webhooks: List<RegisteredWebhook>) {
        val messages = arrayListOf<NewArticlesOutboxMessage>()
        webhooks.forEach { webhook ->
            newArticles.filter { it.storedAt > webhook.registeredAt }.forEach { article ->
                messages.add(
                    NewArticlesOutboxMessage(
                        webhookId = webhook.id,
                        aggregateId = article.articleId,
                        idempotencyKey = idempotencyKey(),
                        payload = objectMapper.writeValueAsString(
                            NewArticlesOutboxMessage.OutboxPayload(
                                articleId = article.articleId,
                                title = article.title,
                                link = article.link,
                                pubDate = article.pubDate,
                                blogName = article.blogName,
                                webhookUrl = webhook.url
                            )
                        )
                    )
                )
            }
        }
        if (messages.isNotEmpty()) eventPublisher.publishEvent(OutboxMessages(messages))
    }

    private fun idempotencyKey(): String {
        return UUID.randomUUID().toString()
    }
}
