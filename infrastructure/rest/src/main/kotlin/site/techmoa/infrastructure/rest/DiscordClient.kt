package site.techmoa.infrastructure.rest

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import site.techmoa.domain.event.OutboxMessages.NewArticlesOutboxMessage.OutboxPayload
import site.techmoa.infrastructure.rest.DiscordWebhookPayload.DiscordEmbed
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class DiscordClient(
    webClient: WebClient.Builder
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val discordClient = webClient.build()

    fun post(message: OutboxPayload) {
        val payload = toDiscordPayload(message)
        val response = discordClient.post()
            .uri(message.webhookUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .retrieve()
            .toBodilessEntity()
            .block()

        if (response == null || !response.statusCode.is2xxSuccessful) {
            log.error("Webhook publishing error. webhookUrl={}, status={}", message.webhookUrl, response?.statusCode)
            throw IllegalStateException("Failed to publish webhook. webhookUrl=${message.webhookUrl}, status=${response?.statusCode}")
        }

        log.info("Published discord webhook. webhookUrl=${message.webhookUrl}, articleId=${message.articleId}")
    }

    private fun toDiscordPayload(message: OutboxPayload): DiscordWebhookPayload {
        val publishedAt = Instant.ofEpochMilli(message.pubDate)
            .atZone(ZoneId.of("Asia/Seoul"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

        val embed = DiscordEmbed(
            title = "[${message.blogName}] ${message.title}",
            url = message.link,
            description = """
                작성일: $publishedAt
            """.trimIndent(),
        )

        return DiscordWebhookPayload(
            username = "TechMoaBot",
            embeds = listOf(embed)
        )
    }
}
