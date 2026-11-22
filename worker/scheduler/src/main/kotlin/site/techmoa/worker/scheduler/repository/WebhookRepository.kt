package site.techmoa.worker.scheduler.repository

import site.techmoa.domain.model.WebhookPlatform

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

data class RegisteredWebhook(val id: Long, val url: String, val registeredAt: LocalDateTime, val platform: WebhookPlatform)

@Repository("SchedulesWebhookRepository")
class WebhookRepository(private val jdbc: JdbcTemplate) {
    fun getValidWebhook(): List<RegisteredWebhook> = jdbc.query(
        "SELECT webhook_id, url, subscribed_at, platform FROM webhook WHERE validity = 'VALID'"
    ) { rs, _ ->
        RegisteredWebhook(rs.getLong("webhook_id"), rs.getString("url"), rs.getTimestamp("subscribed_at").toLocalDateTime(),
            WebhookPlatform.valueOf(rs.getString("platform")))
    }
}
