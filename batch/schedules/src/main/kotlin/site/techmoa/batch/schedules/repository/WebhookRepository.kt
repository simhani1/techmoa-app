package site.techmoa.batch.schedules.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import site.techmoa.domain.model.Webhook
import site.techmoa.domain.model.WebhookPlatform
import site.techmoa.domain.model.WebhookValidity

@Repository(value = "SchedulesWebhookRepository")
class WebhookRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getValidWebhook(): List<Webhook> {
        return findAllBy(WebhookValidity.VALID)
    }

    private fun findAllBy(valid: WebhookValidity): List<Webhook> {
        val sql = """
            SELECT webhook_id, url, validity, platform
            FROM webhook
            WHERE validity = ?
        """.trimIndent()

        val rowMapper = RowMapper { rs, _ ->
            Webhook(
                id = rs.getLong("webhook_id"),
                url = rs.getString("url"),
                validity = WebhookValidity.valueOf(rs.getString("validity")),
                platform = WebhookPlatform.valueOf(rs.getString("platform"))
            )
        }

        return jdbcTemplate.query(
            sql,
            rowMapper,
            valid.name
        )
    }
}
