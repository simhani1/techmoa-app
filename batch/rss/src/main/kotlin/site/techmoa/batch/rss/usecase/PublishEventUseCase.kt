package site.techmoa.batch.rss.usecase

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import site.techmoa.batch.rss.domain.event.NewArticlesCollectedEvent
import site.techmoa.batch.rss.domain.event.NewArticlesCollectedEvent.ArticleCreatedOutboxEvent
import site.techmoa.batch.rss.domain.model.Article
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Component
class PublishEventUseCase(
    private val eventPublisher: ApplicationEventPublisher,
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun publish(articles: List<Article>) {
        if (articles.isEmpty()) {
            log.info("[${this.javaClass.simpleName}] Skip publishing event: no new articles")
            return
        }

        val event = NewArticlesCollectedEvent(
            events = articles.map {
                ArticleCreatedOutboxEvent.pending(
                    blogId = it.blogId,
                    guid = it.guid,
                    idempotencyKey = idempotencyKeyOf(it),
                )
            }
        )
        log.info("[${this.javaClass.simpleName}] Publish NewArticlesCollectedEvent: size=${event.size()}")
        eventPublisher.publishEvent(event)
    }

    private fun idempotencyKeyOf(article: Article): String {
        val pubDate = yyyyMM(article.pubDate)
        return "$pubDate:${article.blogId}:${article.guid}"
    }

    private fun yyyyMM(pubDate: Long): String {
        return DateTimeFormatter.ofPattern("yyyyMM")
            .format(Instant.ofEpochMilli(pubDate).atZone(ZoneOffset.UTC))
    }
}
