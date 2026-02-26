package site.techmoa.domain.event

enum class OutboxStatus {
    PENDING,
    PUBLISHING,
    SUCCESS,
    FAILED
}
