package site.techmoa.worker.scheduler.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.techmoa.domain.event.OutboxMessages
import site.techmoa.domain.event.WebhookRateLimited

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

    data class Delivery(val id: Long, val payload: String)

    @Transactional(readOnly = true)
    fun readyWebhooks(): List<Long> = jdbcTemplate.queryForList(
        """SELECT o.webhook_id FROM outbox_messages o
           LEFT JOIN webhook_retry r ON r.webhook_id = o.webhook_id
           WHERE o.status = 'PENDING' AND o.webhook_id IS NOT NULL
             AND (r.retry_at IS NULL OR r.retry_at <= CURRENT_TIMESTAMP(6))
             AND EXISTS (SELECT 1 FROM webhook_dispatch_clock
                         WHERE id = 1 AND retry_at <= CURRENT_TIMESTAMP(6))
           GROUP BY o.webhook_id ORDER BY MIN(o.outbox_message_id)""",
        Long::class.java
    )

    @Transactional(readOnly = true)
    fun pending(webhookId: Long): List<Delivery> = jdbcTemplate.query(
        """SELECT outbox_message_id, payload FROM outbox_messages
           WHERE webhook_id = ? AND status = 'PENDING'
           ORDER BY outbox_message_id LIMIT 20""",
        RowMapper { rs, _ -> Delivery(rs.getLong(1), rs.getString(2)) },
        webhookId
    )

    @Transactional
    fun saveResults(webhookId: Long, sent: List<Long>, failures: Map<Long, String>, limit: WebhookRateLimited?) {
        if (sent.isNotEmpty()) {
            val placeholders = sent.joinToString { "?" }
            jdbcTemplate.update(
                """UPDATE outbox_messages SET status = 'SUCCESS', published_at = CURRENT_TIMESTAMP(6),
                   last_error_message = NULL WHERE outbox_message_id IN ($placeholders)""",
                *sent.toTypedArray()
            )
        }
        if (failures.isNotEmpty()) {
            jdbcTemplate.batchUpdate(
                "UPDATE outbox_messages SET status = 'FAIL', last_error_message = ? WHERE outbox_message_id = ?",
                failures.map { (id, reason) -> arrayOf<Any>(reason, id) }
            )
        }
        if (limit != null) defer(webhookId, limit.delayMillis, limit.global)
    }

    private fun defer(webhookId: Long, delayMillis: Long, global: Boolean) {
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
