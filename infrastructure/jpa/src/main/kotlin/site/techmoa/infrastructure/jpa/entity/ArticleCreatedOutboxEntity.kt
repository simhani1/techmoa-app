package site.techmoa.infrastructure.jpa.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "article_created_outbox",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_article_created_outbox_idempotency_key",
            columnNames = ["idempotency_key"]
        )
    ]
)
class ArticleCreatedOutboxEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_created_outbox_id", nullable = false)
    var id: Long = 0L,

    @Column(name = "article_id", nullable = false)
    val articleId: Long,

    @Lob
    @Column(name = "payload", nullable = false)
    val payload: String,

    idempotencyKey: String,
) : OutboxBaseEntity(idempotencyKey = idempotencyKey) {

    companion object {
        fun of(
            articleId: Long,
            payload: String,
            idempotencyKey: String
        ): ArticleCreatedOutboxEntity {
            return ArticleCreatedOutboxEntity(
                articleId = articleId,
                payload = payload,
                idempotencyKey = idempotencyKey
            )
        }
    }
}
