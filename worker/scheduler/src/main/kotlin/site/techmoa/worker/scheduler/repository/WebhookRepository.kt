package site.techmoa.worker.scheduler.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

data class RegisteredWebhook(val id: Long, val url: String, val registeredAt: LocalDateTime)

@Repository("SchedulesWebhookRepository")
class WebhookRepository(private val jdbc: JdbcTemplate) {
    fun getValidWebhook(): List<RegisteredWebhook> = jdbc.query(
        "SELECT webhook_id, url, subscribed_at FROM webhook WHERE validity = 'VALID'"
    ) { rs, _ ->
        RegisteredWebhook(rs.getLong("webhook_id"), rs.getString("url"), rs.getTimestamp("subscribed_at").toLocalDateTime())
    }
}
