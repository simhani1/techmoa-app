package site.techmoa.worker.scheduler.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage.OutboxPayload
import site.techmoa.domain.event.WebhookRateLimited
import site.techmoa.domain.event.WebhookSender
import site.techmoa.worker.scheduler.repository.OutboxRepository

@Service
class OutboxDispatchService(
    private val repository: OutboxRepository,
    private val sender: WebhookSender,
    private val mapper: ObjectMapper,
    private val transaction: TransactionTemplate,
) {
    fun dispatchPending() {
        var preferredWebhook: Long? = null
        while (!Thread.currentThread().isInterrupted) {
            preferredWebhook = transaction.execute {
                if (!repository.acquireDispatcher()) return@execute null
                val delivery = repository.nextDelivery(preferredWebhook) ?: return@execute null
                try {
                    sender.publish(mapper.readValue<OutboxPayload>(delivery.payload))
                } catch (limit: WebhookRateLimited) {
                    repository.defer(delivery.webhookId, limit.delayMillis, limit.global)
                    return@execute delivery.webhookId
                } catch (ex: Exception) {
                    // Exception messages from the HTTP client can contain the webhook token.
                    repository.markFailed(delivery.id, ex.javaClass.simpleName)
                    return@execute delivery.webhookId
                }
                repository.markSuccess(delivery.id)
                delivery.webhookId
            } ?: return
        }
    }
}
