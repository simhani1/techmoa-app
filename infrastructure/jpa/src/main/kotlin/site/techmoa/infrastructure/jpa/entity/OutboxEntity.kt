package site.techmoa.infrastructure.jpa.entity

import jakarta.persistence.*
import site.techmoa.domain.event.OutboxStatus
import java.time.LocalDateTime

@Entity
@Table(
    name = "outbox",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_outbox_idempotency_key",
            columnNames = ["idempotency_key"]
        )
    ]
)
class OutboxEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id", nullable = false)
    var id: Long = 0L,

    @Column(name = "blog_id", nullable = false)
    val blogId: Long,

    @Column(name = "article_id", nullable = false)
    val articleId: Long,

    @Column(name = "idempotency_key", nullable = false, length = 100)
    val idempotencyKey: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private var _status: OutboxStatus = OutboxStatus.PENDING,

    @Column(name = "retry_count", nullable = false)
    private var _retryCount: Int = 0,

    @Column(name = "published_at")
    private var _publishedAt: LocalDateTime? = null,

    @Column(name = "last_error_message", length = 1000)
    private var _lastErrorMessage: String? = null,
) : BaseEntity() {

    companion object {
        fun of(
            blogId: Long,
            articleId: Long,
            idempotencyKey: String
        ): OutboxEntity {
            return OutboxEntity(
                blogId = blogId,
                articleId = articleId,
                idempotencyKey = idempotencyKey
            )
        }
    }

    val status: OutboxStatus
        get() = _status

    val retryCount: Int
        get() = _retryCount

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
        _retryCount += 1
        _lastErrorMessage = errorMessage
    }
}
