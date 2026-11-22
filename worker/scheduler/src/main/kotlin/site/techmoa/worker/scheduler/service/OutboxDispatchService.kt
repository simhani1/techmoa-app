package site.techmoa.worker.scheduler.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import site.techmoa.domain.event.forPlatform
import site.techmoa.domain.model.WebhookPlatform
import org.springframework.stereotype.Service
import site.techmoa.domain.event.ArticleNotification
import site.techmoa.domain.event.WebhookRateLimited
import site.techmoa.domain.event.WebhookSender
import site.techmoa.worker.scheduler.repository.OutboxRepository

@Service
class OutboxDispatchService(
    private val repository: OutboxRepository,
    private val senders: List<WebhookSender>,
    private val mapper: ObjectMapper,
) {
    fun dispatchPending() {
        val pausedPlatforms = mutableSetOf<WebhookPlatform>()
        for (destination in repository.findReadyDestinations()) {
            if (destination.platform in pausedPlatforms) continue
            val rateLimit = dispatchWebhook(destination)
            if (rateLimit?.global == true) pausedPlatforms.add(destination.platform)
        }
    }

    private fun dispatchWebhook(destination: OutboxRepository.Destination): WebhookRateLimited? {
        while (!Thread.currentThread().isInterrupted) {
            val pendingDeliveries = repository.findPendingDeliveries(destination)
            if (pendingDeliveries.isEmpty()) return null

            val sentOutboxIds = mutableListOf<Long>()
            val failureReasonsByOutboxId = mutableMapOf<Long, String>()
            var rateLimit: WebhookRateLimited? = null
            for (delivery in pendingDeliveries) {
                try {
                    val sender = senders.forPlatform(destination.platform)
                    val notification = mapper.readValue<ArticleNotification>(delivery.payload)
                    sender.publish(notification)
                    sentOutboxIds.add(delivery.outboxId)
                } catch (ex: WebhookRateLimited) {
                    rateLimit = ex
                    break
                } catch (ex: Exception) {
                    // HTTP exception messages may contain the webhook token.
                    failureReasonsByOutboxId[delivery.outboxId] = ex.javaClass.simpleName
                }
            }
            repository.recordDeliveryResults(destination, sentOutboxIds, failureReasonsByOutboxId, rateLimit)
            if (rateLimit != null) return rateLimit
        }
        return null
    }
}
