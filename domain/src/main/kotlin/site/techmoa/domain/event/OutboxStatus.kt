package site.techmoa.domain.event

enum class OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
