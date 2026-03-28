package site.techmoa.domain.event

import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage.OutboxPayload

interface WebhookMessageBroker {
    fun publish(message: OutboxPayload)
}