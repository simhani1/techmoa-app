package site.techmoa.infrastructure.kafka.publisher

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import site.techmoa.domain.event.OutboxMessages
import site.techmoa.domain.event.WebhookMessageBroker
import java.util.concurrent.ExecutionException

@Component
class WebhookKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : WebhookMessageBroker {

    companion object {
        const val WEBHOOK_DISCORD = "webhook-discord"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun publish(message: OutboxMessages.NewArticlesOutboxMessage.OutboxPayload) {
        val payload = objectMapper.writeValueAsString(message)
        try {
            kafkaTemplate.send(WEBHOOK_DISCORD, payload).get()
            log.info(
                "Published webhook message to Kafka. topic={}, articleId={}, webhookUrl={}",
                WEBHOOK_DISCORD,
                message.articleId,
                message.webhookUrl,
            )
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
            log.error(
                "Interrupted while publishing webhook message to Kafka. topic={}, articleId={}, webhookUrl={}",
                WEBHOOK_DISCORD,
                message.articleId,
                message.webhookUrl,
                ex,
            )
            throw IllegalStateException(
                "Interrupted while publishing webhook message to Kafka. topic=$WEBHOOK_DISCORD, articleId=${message.articleId}, webhookUrl=${message.webhookUrl}",
                ex,
            )
        } catch (ex: ExecutionException) {
            throw IllegalStateException(
                "Failed to publish webhook message to Kafka. topic=$WEBHOOK_DISCORD, articleId=${message.articleId}, webhookUrl=${message.webhookUrl}",
                ex.cause ?: ex,
            )
        }
    }
}