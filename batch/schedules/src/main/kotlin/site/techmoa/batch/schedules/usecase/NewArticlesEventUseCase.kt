package site.techmoa.batch.schedules.usecase

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import site.techmoa.batch.schedules.event.NewArticlesEvents
import site.techmoa.domain.model.Article
import site.techmoa.domain.model.Webhook
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class NewArticlesEventUseCase(
    private val eventPublisher: ApplicationEventPublisher
) {

    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    }

    fun publish(articles: List<Article>, webhooks: List<Webhook>) {
        val events = arrayListOf<NewArticlesEvents.NewArticlesEvent>()
        webhooks.forEach { webhook ->
            articles.forEach { article ->
                events.add(
                    NewArticlesEvents.NewArticlesEvent(
                        article = article,
                        webhook = webhook,
                        idempotencyKey = idempotencyKey(
                            articleId = article.id,
                            webhookId = webhook.id
                        )
                    )
                )
            }
        }
        eventPublisher.publishEvent(NewArticlesEvents(events))
    }

    private fun idempotencyKey(
        articleId: Long,
        webhookId: Long
    ): String {
        val eventTime = LocalDateTime.now().format(dateTimeFormatter)
        return "${eventTime}:article:${articleId}:webhook:${webhookId}"
    }
}
