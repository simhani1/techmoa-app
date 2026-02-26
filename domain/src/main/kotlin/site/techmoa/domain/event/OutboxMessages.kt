package site.techmoa.domain.event

data class OutboxMessages(
    val messages: List<NewArticlesOutboxMessage>
) {
    data class NewArticlesOutboxMessage(
        val outboxMessageId: Long = 0,
        val eventType: EventType = EventType.NEW_ARTICLE,
        val aggregateType: String = "ARTICLE",      // e.g. "article"
        val aggregateId: Long,        // e.g. articleId.toString()
        val idempotencyKey: String,
        val payloadType: String = "application/json",
        val payload: String,
        val status: OutboxStatus = OutboxStatus.PENDING,
    ) {
        data class OutboxPayload(
            val articleId: Long,
            val title: String,
            val link: String,
            val pubDate: Long,
            val blogName: String,
            val webhookUrl: String,
        )
    }
}
