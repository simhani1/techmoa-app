package site.techmoa.batch.rss.domain.event

enum class OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
