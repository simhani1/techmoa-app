package site.techmoa.domain.event

import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage.OutboxPayload

class WebhookRateLimited(val delayMillis: Long, val global: Boolean) : RuntimeException()

interface WebhookSender {
    fun publish(message: OutboxPayload)
}