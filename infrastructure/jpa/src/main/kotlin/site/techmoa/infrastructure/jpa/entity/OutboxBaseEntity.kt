package site.techmoa.infrastructure.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.MappedSuperclass
import site.techmoa.domain.event.OutboxStatus
import java.time.LocalDateTime

@MappedSuperclass
abstract class OutboxBaseEntity(
    @Column(name = "idempotency_key", nullable = false, length = 150)
    val idempotencyKey: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    protected var _status: OutboxStatus = OutboxStatus.PENDING,

    @Column(name = "published_at")
    protected var _publishedAt: LocalDateTime? = null,

    @Column(name = "last_error_message", length = 1000)
    protected var _lastErrorMessage: String? = null,
) : BaseEntity() {

    val status: OutboxStatus
        get() = _status

    val publishedAt: LocalDateTime?
        get() = _publishedAt

    val lastErrorMessage: String?
        get() = _lastErrorMessage

    fun markPublished(publishedAt: LocalDateTime = LocalDateTime.now()) {
        _status = OutboxStatus.PUBLISHED
        _publishedAt = publishedAt
        _lastErrorMessage = null
    }

    fun markFailed(errorMessage: String?) {
        _status = OutboxStatus.FAILED
        _lastErrorMessage = errorMessage
    }
}
