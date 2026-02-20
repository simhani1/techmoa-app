package site.techmoa.batch.rss.domain.event

data class NewArticlesCollectedEvent(
    val events: List<ArticleCreatedOutboxEvent>
) {
    data class ArticleCreatedOutboxEvent private constructor(
        val blogId: Long,
        val guid: String,
        val idempotencyKey: String,
        val status: OutboxStatus
    ) {
        companion object {
            fun pending(
                blogId: Long,
                guid: String,
                idempotencyKey: String
            ): ArticleCreatedOutboxEvent {
                return ArticleCreatedOutboxEvent(
                    blogId,
                    guid,
                    idempotencyKey,
                    OutboxStatus.PENDING
                )
            }
        }
    }

    fun size(): Int = events.size
}
