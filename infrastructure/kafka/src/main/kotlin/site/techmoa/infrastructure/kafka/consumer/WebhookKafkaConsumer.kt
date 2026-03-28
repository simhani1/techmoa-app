package site.techmoa.infrastructure.kafka.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component
import site.techmoa.domain.event.OutboxMessages
import site.techmoa.infrastructure.rest.DiscordClient

@Component
class WebhookKafkaConsumer(
    private val client : DiscordClient,
    private val objectMapper: ObjectMapper,
) {

    companion object {
        private const val TOPIC_NAME = "webhook-discord"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = [TOPIC_NAME],
        containerFactory = "kafkaListenerContainerFactory",
    )
    @RetryableTopic(
        attempts = "4",
        include = [IllegalStateException::class],
        backoff = Backoff(
            delay = 4_000,
            multiplier = 2.0,
            maxDelay = 60_000,
            random = true,
        ),
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
    )
    fun consume(message: String) {
        try {
            val payload = objectMapper.readValue<OutboxMessages.NewArticlesOutboxMessage.OutboxPayload>(message)
            client.post(payload)
            log.info(
                "Consumed webhook message from Kafka and published to Discord. topic={}, articleId={}, webhookUrl={}",
                TOPIC_NAME,
                payload.articleId,
                payload.webhookUrl,
            )
        } catch (ex: Exception) {
            log.error(
                "Failed to consume webhook message from Kafka. topic={}",
                TOPIC_NAME,
                ex,
            )
            throw IllegalStateException(
                "Failed to consume webhook message from Kafka. topic=$TOPIC_NAME",
                ex,
            )
        }
    }
}