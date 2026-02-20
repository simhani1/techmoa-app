package site.techmoa.batch.rss.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import site.techmoa.batch.rss.domain.event.NewArticlesCollectedEvent.ArticleCreatedOutboxEvent
import site.techmoa.batch.rss.port.ArticleCreatedOutboxPort
import java.sql.Timestamp
import java.time.Instant

@Repository
class ArticleCreatedOutboxRepository(
    private val jdbcTemplate: JdbcTemplate
) : ArticleCreatedOutboxPort {

    override fun saveAll(outboxes: List<ArticleCreatedOutboxEvent>) {
        val now = Timestamp.from(Instant.now())
        val sql = """
            INSERT IGNORE
            INTO article_created_outbox (
                payload,
                idempotency_key,
                status,
                last_error_message,
                created_at,
                updated_at
            )
            VALUES (
                ?,
                ?,
                ?,
                NULL,
                ?,
                ?
            )
        """.trimIndent()

        val batchArgs = outboxes.map { outbox ->
            arrayOf(
                outbox.payload,
                outbox.idempotencyKey,
                outbox.status.toString(),
                now,
                now
            )
        }

        jdbcTemplate.batchUpdate(sql, batchArgs)
    }
}
