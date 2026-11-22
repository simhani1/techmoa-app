package site.techmoa.worker.scheduler.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.techmoa.domain.event.OutboxMessages
import site.techmoa.domain.event.OutboxStatus
import java.sql.Timestamp
import java.time.Instant

@Repository(value = "SchedulesOutboxRepository")
class OutboxRepository(
    private val jdbcTemplate: JdbcTemplate,
) {

    fun save(messages: OutboxMessages) {
        val sql = """
        INSERT INTO outbox_messages (
            webhook_id, event_type,
            aggregate_type,
            aggregate_id,
            idempotency_key,
            payload,
            payload_type,
            status
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE webhook_id = VALUES(webhook_id)
    """.trimIndent()

        val args: List<Array<Any?>> = messages.messages.map { e ->
            arrayOf(
                e.webhookId, e.eventType.name,
                e.aggregateType,
                e.aggregateId,
                e.idempotencyKey,
                e.payload,
                e.payloadType,
                e.status.name,
            )
        }

        jdbcTemplate.batchUpdate(sql, args)
    }

    @Transactional
    fun markSuccess(outboxId: Long) {
        val now = Timestamp.from(Instant.now())
        val sql = """
            UPDATE outbox_messages
            SET status = ?,
                published_at = ?,
                last_error_message = NULL
            WHERE outbox_message_id = ?
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            OutboxStatus.SUCCESS.name,
            now,
            outboxId
        )
    }

    @Transactional
    fun markFailed(outboxId: Long, errorMessage: String) {
        val sql = """
            UPDATE outbox_messages
            SET status = ?,
                published_at = NULL,
                last_error_message = ?
            WHERE outbox_message_id = ?;
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            OutboxStatus.FAIL.name,
            errorMessage,
            outboxId
        )
    }

    data class Delivery(val id: Long, val webhookId: Long, val payload: String)

    // The caller holds this singleton row lock until one bounded HTTP request completes.
    fun acquireDispatcher(): Boolean = jdbcTemplate.queryForList(
        """SELECT id FROM webhook_dispatch_clock
           WHERE id = 1 AND retry_at <= CURRENT_TIMESTAMP(6) FOR UPDATE SKIP LOCKED""",
        Long::class.java
    ).isNotEmpty()

    fun nextDelivery(preferredWebhook: Long?): Delivery? {
        if (preferredWebhook != null) {
            findPending(preferredWebhook)?.let { return it }
        }
        return findPending(null)
    }

    private fun findPending(webhookId: Long?): Delivery? {
        val destinationFilter = if (webhookId == null) "" else "AND o.webhook_id = ?"
        val args = listOfNotNull(webhookId).toTypedArray()
        return jdbcTemplate.query(
            """SELECT o.outbox_message_id, o.webhook_id, o.payload
               FROM outbox_messages o
               LEFT JOIN webhook_retry r ON r.webhook_id = o.webhook_id
               WHERE o.status = 'PENDING' AND o.webhook_id IS NOT NULL
                 $destinationFilter
                 AND (r.retry_at IS NULL OR r.retry_at <= CURRENT_TIMESTAMP(6))
               ORDER BY o.outbox_message_id LIMIT 1""",
            RowMapper { rs, _ -> Delivery(rs.getLong(1), rs.getLong(2), rs.getString(3)) },
            *args
        ).firstOrNull()
    }

    fun defer(webhookId: Long, delayMillis: Long, global: Boolean) {
        val micros = Math.multiplyExact(delayMillis, 1000L)
        if (global) {
            jdbcTemplate.update(
                "UPDATE webhook_dispatch_clock SET retry_at = TIMESTAMPADD(MICROSECOND, ?, CURRENT_TIMESTAMP(6)) WHERE id = 1",
                micros
            )
        } else {
            jdbcTemplate.update(
                """INSERT INTO webhook_retry (webhook_id, retry_at)
                   VALUES (?, TIMESTAMPADD(MICROSECOND, ?, CURRENT_TIMESTAMP(6)))
                   ON DUPLICATE KEY UPDATE retry_at = VALUES(retry_at)""",
                webhookId, micros
            )
        }
    }
}
