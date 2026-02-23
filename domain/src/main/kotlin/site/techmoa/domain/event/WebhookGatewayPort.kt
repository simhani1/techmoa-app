package site.techmoa.domain.event

interface WebhookGatewayPort {
    fun publish(message: NewArticlesEvents.NewArticlesEvent)
}