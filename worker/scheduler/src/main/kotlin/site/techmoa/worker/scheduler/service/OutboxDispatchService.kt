package site.techmoa.worker.scheduler.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage.OutboxPayload
import site.techmoa.domain.event.WebhookMessageBroker
import site.techmoa.worker.scheduler.repository.ArticleRepository
import site.techmoa.worker.scheduler.repository.OutboxRepository
import site.techmoa.worker.scheduler.repository.WebhookRepository

@Service
class OutboxDispatchService(
    private val outboxRepository: OutboxRepository,
    private val articleRepository: ArticleRepository,
    private val webhookRepository: WebhookRepository,
    private val messageBroker: WebhookMessageBroker,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun dispatchPending() {
        val pendingMessages = outboxRepository.claimPending()
        if (pendingMessages.isEmpty()) return

        pendingMessages.forEach { message ->
            try {
                val json = message.payload
                val payload = objectMapper.readValue<OutboxPayload>(json)
                messageBroker.publish(payload)
                outboxRepository.markSuccess(message.outboxMessageId)
            } catch (ex: Exception) {
                log.error(
                    "Failed to publish outbox message. outboxMessageId={}",
                    message.outboxMessageId,
                    ex
                )
                outboxRepository.markFailed(
                    message.outboxMessageId,
                    ex.message?.take(1000) ?: "Unknown error"
                )
            }
        }
    }
}
