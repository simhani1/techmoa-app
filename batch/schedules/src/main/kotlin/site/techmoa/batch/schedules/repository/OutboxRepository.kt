package site.techmoa.batch.schedules.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import site.techmoa.domain.event.NewArticlesEvents
import site.techmoa.domain.event.OutboxStatus
import java.sql.Timestamp
import java.time.Instant

@Repository(value = "SchedulesOutboxRepository")
class OutboxRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    data class OutboxMessage(
        val id: Long,
        val blogId: Long,
        val guid: String,
        val idempotencyKey: String,
    )

    fun save(event: NewArticlesEvents) {
        val now = Timestamp.from(Instant.now())
        val sql = """
            INSERT INTO article_created_outbox (
                blog_id,
                guid,
                idempotency_key,
                status,
                published_at,
                last_error_message,
                created_at,
                updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(
            sql,
            event.events.map { e ->
                arrayOf(
                    e.article.blogId,
                    e.article.guid,
                    e.idempotencyKey,
                    OutboxStatus.PENDING.name,
                    null,
                    null,
                    now,
                    now
                )
            }
        )
    }

    fun findPending(): List<OutboxMessage> {
        val sql = """
            SELECT article_created_outbox_id, blog_id, guid, idempotency_key
            FROM article_created_outbox
            WHERE status = ?
            ORDER BY created_at ASC
        """.trimIndent()

        val rowMapper = RowMapper { rs, _ ->
            OutboxMessage(
                id = rs.getLong("article_created_outbox_id"),
                blogId = rs.getLong("blog_id"),
                guid = rs.getString("guid"),
                idempotencyKey = rs.getString("idempotency_key")
            )
        }

        return jdbcTemplate.query(
            sql,
            rowMapper,
            OutboxStatus.PENDING.name,
        )
    }

    fun markPublished(outboxId: Long) {
        val now = Timestamp.from(Instant.now())
        val sql = """
            UPDATE article_created_outbox
            SET status = ?,
                published_at = ?,
                last_error_message = NULL,
                updated_at = ?
            WHERE article_created_outbox_id = ?
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            OutboxStatus.PUBLISHED.name,
            now,
            now,
            outboxId
        )
    }

    fun markFailed(outboxId: Long, errorMessage: String) {
        val now = Timestamp.from(Instant.now())
        val sql = """
            UPDATE article_created_outbox
            SET status = ?,
                published_at = NULL,
                last_error_message = ?,
                updated_at = ?
            WHERE article_created_outbox_id = ?
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            OutboxStatus.FAILED.name,
            errorMessage,
            now,
            outboxId
        )
    }
}
