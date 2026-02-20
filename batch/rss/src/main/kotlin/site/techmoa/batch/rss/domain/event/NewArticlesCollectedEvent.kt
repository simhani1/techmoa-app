package site.techmoa.batch.rss.domain.event

data class NewArticlesCollectedEvent(
    val events: List<ArticleCreatedOutboxEvent>
) {
    data class ArticleCreatedOutboxEvent private constructor(
        val payload: String,
        val idempotencyKey: String,
        val status: OutboxStatus
    ) {
        companion object {
            fun pending(payload: String, idempotencyKey: String): ArticleCreatedOutboxEvent {
                return ArticleCreatedOutboxEvent(
                    payload,
                    idempotencyKey,
                    OutboxStatus.PENDING
                )
            }
        }
    }

    fun size(): Int = events.size
}
