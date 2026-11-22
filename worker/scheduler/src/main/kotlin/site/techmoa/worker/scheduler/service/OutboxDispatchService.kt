package site.techmoa.worker.scheduler.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage.OutboxPayload
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
        for (webhookId in repository.readyWebhooks()) {
            while (!Thread.currentThread().isInterrupted) {
                val batch = repository.pending(webhookId)
                if (batch.isEmpty()) break

                val sent = mutableListOf<Long>()
                val failures = mutableMapOf<Long, String>()
                var limit: WebhookRateLimited? = null
                for (delivery in batch) {
                    try {
                        sender.publish(mapper.readValue<OutboxPayload>(delivery.payload))
                        sent.add(delivery.id)
                    } catch (ex: WebhookRateLimited) {
                        limit = ex
                        break
                    } catch (ex: Exception) {
                        // HTTP exception messages may contain the webhook token.
                        failures[delivery.id] = ex.javaClass.simpleName
                    }
                }
                repository.saveResults(webhookId, sent, failures, limit)
                if (limit?.global == true) return
                if (limit != null) break
            }
        }
    }
}
