package site.techmoa.batch.schedules.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import site.techmoa.batch.schedules.event.NewArticlesEvents
import site.techmoa.domain.event.OutboxStatus
import java.sql.Timestamp
import java.time.Instant

@Repository(value = "SchedulesOutboxRepository")
class OutboxRepository(
    private val jdbcTemplate: JdbcTemplate
) {

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
}
