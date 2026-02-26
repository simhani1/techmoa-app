package site.techmoa.domain.event

import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage.OutboxPayload

interface WebhookGatewayPort {
    fun publish(message: OutboxPayload)
}