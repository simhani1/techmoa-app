package site.techmoa.batch.schedules.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import site.techmoa.batch.schedules.repository.ArticleRepository
import site.techmoa.batch.schedules.repository.OutboxRepository
import site.techmoa.batch.schedules.repository.WebhookRepository
import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage.OutboxPayload
import site.techmoa.domain.event.WebhookGatewayPort

@Service
class OutboxDispatchService(
    private val outboxRepository: OutboxRepository,
    private val articleRepository: ArticleRepository,
    private val webhookRepository: WebhookRepository,
    private val webhookGatewayPort: WebhookGatewayPort,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val BATCH_SIZE = 10
    }

    fun dispatchPending() {
        val pendingMessages = outboxRepository.claimPending(BATCH_SIZE)
        if (pendingMessages.isEmpty()) return

        pendingMessages.forEach { message ->
            try {
                val json = message.payload
                val payload = objectMapper.readValue<OutboxPayload>(json)
                webhookGatewayPort.publish(payload)
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
