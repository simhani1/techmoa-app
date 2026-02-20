package site.techmoa.infrastructure.jpa.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "article_created_outbox",
    indexes = [
        Index(
            name = "idx_article_created_outbox_blog_id_guid",
            columnList = "blog_id, guid"
        )
    ],
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

    @Column(name = "blog_id", nullable = false)
    val blogId: Long,

    @Column(name = "guid", nullable = false, length = 600)
    val guid: String,

    idempotencyKey: String,
) : OutboxBaseEntity(idempotencyKey = idempotencyKey)
