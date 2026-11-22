package site.techmoa.worker.scheduler.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import site.techmoa.domain.event.ArticleNotification
import site.techmoa.domain.event.WebhookRateLimited
import site.techmoa.domain.event.WebhookSender
import site.techmoa.worker.scheduler.repository.OutboxRepository

@Service
class OutboxDispatchService(
    private val repository: OutboxRepository,
    private val sender: WebhookSender,
    private val mapper: ObjectMapper,
) {
    fun dispatchPending() {
        for (webhookId in repository.findReadyWebhookIds()) {
            while (!Thread.currentThread().isInterrupted) {
                val pendingDeliveries = repository.findPendingDeliveries(webhookId)
                if (pendingDeliveries.isEmpty()) break

                val sentOutboxIds = mutableListOf<Long>()
                val failureReasonsByOutboxId = mutableMapOf<Long, String>()
                var rateLimit: WebhookRateLimited? = null
                for (delivery in pendingDeliveries) {
                    try {
                        sender.publish(mapper.readValue<ArticleNotification>(delivery.payload))
                        sentOutboxIds.add(delivery.outboxId)
                    } catch (ex: WebhookRateLimited) {
                        rateLimit = ex
                        break
                    } catch (ex: Exception) {
                        // HTTP exception messages may contain the webhook token.
                        failureReasonsByOutboxId[delivery.outboxId] = ex.javaClass.simpleName
                    }
                }
                repository.recordDeliveryResults(webhookId, sentOutboxIds, failureReasonsByOutboxId, rateLimit)
                if (rateLimit?.global == true) return
                if (rateLimit != null) break
            }
        }
    }
}
