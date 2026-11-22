package site.techmoa.infrastructure.rest

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.reactive.function.client.WebClientResponseException
import site.techmoa.domain.event.WebhookSender
import site.techmoa.domain.event.WebhookRateLimited
import java.time.Duration
import kotlin.math.ceil
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
    webClient: WebClient.Builder,
    private val mapper: ObjectMapper
) : WebhookSender {
    private companion object {
        const val RETRY_DELAY_MULTIPLIER = 1.2
    }

    private val discordClient = webClient.build()

    override fun publish(message: OutboxPayload) {
        try {
            discordClient.post()
                .uri(message.webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(toDiscordPayload(message))
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(15))
        } catch (ex: WebClientResponseException.TooManyRequests) {
            val body = runCatching { mapper.readTree(ex.responseBodyAsString) }.getOrNull()
            val seconds = listOfNotNull(
                ex.headers.getFirst("Retry-After")?.toDoubleOrNull(),
                body?.get("retry_after")?.asDouble(),
            ).filter { it.isFinite() && it > 0 }.maxOrNull() ?: 1.0
            val global = ex.headers.getFirst("X-RateLimit-Global") == "true" ||
                ex.headers.getFirst("X-RateLimit-Scope") == "global" ||
                body?.get("global")?.asBoolean() == true
            throw WebhookRateLimited(ceil(seconds * 1000 * RETRY_DELAY_MULTIPLIER).toLong(), global)
        }
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
