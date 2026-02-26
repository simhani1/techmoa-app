package site.techmoa.batch.schedules.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.techmoa.domain.event.EventType
import site.techmoa.domain.event.OutboxMessages
import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage
import site.techmoa.domain.event.OutboxStatus
import java.sql.Timestamp
import java.time.Instant

@Repository(value = "SchedulesOutboxRepository")
class OutboxRepository(
    private val jdbcTemplate: JdbcTemplate,
) {

    data class OutboxMessage(
        val id: Long,
        val blogId: Long,
        val guid: String,
        val idempotencyKey: String,
    )

    fun save(messages: OutboxMessages) {
        val sql = """
        INSERT INTO outbox_messages (
            event_type,
            aggregate_type,
            aggregate_id,
            idempotency_key,
            payload,
            payload_type,
            status
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
    """.trimIndent()

        val args: List<Array<Any?>> = messages.messages.map { e ->
            arrayOf(
                e.eventType.name,
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
        val now = Timestamp.from(Instant.now())
        val sql = """
            UPDATE outbox_messages
            SET status = ?,
                published_at = NULL,
                last_error_message = ?
            WHERE outbox_message_id = ?;
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            OutboxStatus.FAILED.name,
            errorMessage,
            outboxId
        )
    }

    @Transactional
    fun claimPending(batchSize: Int): List<NewArticlesOutboxMessage> {
        val pendingMessages = findPending(batchSize)
        markPublishing(pendingMessages.map { it.outboxMessageId })
        return pendingMessages
    }

    private fun findPending(batchSize: Int): List<NewArticlesOutboxMessage> {
        val sql = """
            SELECT
                outbox_message_id,
                event_type,
                aggregate_type,
                aggregate_id,
                idempotency_key,
                payload,
                payload_type,
                status,
                created_at
            FROM outbox_messages
            WHERE status = 'PENDING'
            ORDER BY created_at ASC
            limit $batchSize
            FOR UPDATE SKIP LOCKED;
        """.trimIndent()

        val rowMapper = RowMapper { rs, _ ->
            NewArticlesOutboxMessage(
                outboxMessageId = rs.getLong("outbox_message_id"),
                eventType = EventType.valueOf(rs.getString("event_type")),
                aggregateType = rs.getString("aggregate_type"),
                aggregateId = rs.getLong("aggregate_id"),
                idempotencyKey = rs.getString("idempotency_key"),
                payload = rs.getString("payload"),
                payloadType = rs.getString("payload_type"),
                status = OutboxStatus.valueOf(rs.getString("status"))
            )
        }

        return jdbcTemplate.query(sql, rowMapper)
    }

    private fun markPublishing(outboxIds: List<Long>) {
        val now = Timestamp.from(Instant.now())
        val sql = """
            UPDATE outbox_messages
            SET status = 'PUBLISHING',
                published_at = ?,
                last_error_message = NULL
            WHERE outbox_message_id = ?
            AND status = 'PENDING'
        """.trimIndent()

        val args = outboxIds.map { id ->
            arrayOf(now, id)
        }

        jdbcTemplate.batchUpdate(sql, args)
    }
}
