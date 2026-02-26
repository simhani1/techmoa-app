package site.techmoa.batch.schedules.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import site.techmoa.batch.schedules.dto.NewArticleDto
import site.techmoa.domain.event.OutboxMessages
import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage
import site.techmoa.domain.model.Webhook
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class NewArticlesEventUseCase(
    private val objectMapper: ObjectMapper,
    private val eventPublisher: ApplicationEventPublisher
) {

    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    }

    fun publish(newArticles: List<NewArticleDto>, webhooks: List<Webhook>) {
        val messages = arrayListOf<NewArticlesOutboxMessage>()
        webhooks.forEach { webhook ->
            newArticles.forEach { article ->
                messages.add(
                    NewArticlesOutboxMessage(
                        aggregateId = article.articleId,
                        idempotencyKey = idempotencyKey(article.articleId, webhook.id),
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
        eventPublisher.publishEvent(OutboxMessages(messages))
    }

    private fun idempotencyKey(
        articleId: Long,
        webhookId: Long
    ): String {
        val eventTime = LocalDateTime.now().format(dateTimeFormatter)
        return "${eventTime}:article:${articleId}:webhook:${webhookId}"
    }
}
