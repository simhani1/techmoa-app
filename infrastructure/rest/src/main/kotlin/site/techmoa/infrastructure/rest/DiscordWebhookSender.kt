package site.techmoa.infrastructure.rest

import site.techmoa.domain.model.WebhookPlatform
import java.net.URI
import site.techmoa.domain.exception.InvalidWebhookUrlException

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.reactive.function.client.WebClientResponseException
import site.techmoa.domain.event.WebhookSender
import site.techmoa.domain.event.WebhookRateLimited
import java.time.Duration
import kotlin.math.ceil
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import site.techmoa.domain.event.ArticleNotification
import site.techmoa.infrastructure.rest.DiscordWebhookPayload.DiscordEmbed
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class DiscordWebhookSender(
    webClient: WebClient.Builder,
    private val mapper: ObjectMapper
) : WebhookSender {
    private companion object {
        val WEBHOOK_PATH = Regex("/api/(?:v[0-9]+/)?webhooks/[0-9]+/[A-Za-z0-9_-]+")
        const val RETRY_DELAY_MULTIPLIER = 1.2
    }

    private val discordClient = webClient.build()

    override val platform = WebhookPlatform.DISCORD

    override fun validateWebhookUrl(url: String) {
        val uri = try {
            URI(url)
        } catch (_: java.net.URISyntaxException) {
            throw InvalidWebhookUrlException("Invalid Discord webhook URL")
        }
        val valid = uri.scheme.equals("https", ignoreCase = true) &&
            uri.host.equals("discord.com", ignoreCase = true) &&
            uri.rawUserInfo == null && uri.port in setOf(-1, 443) &&
            uri.rawQuery == null && uri.rawFragment == null &&
            WEBHOOK_PATH.matches(uri.rawPath.orEmpty())
        if (!valid) throw InvalidWebhookUrlException("Invalid Discord webhook URL")
    }

    override fun publish(message: ArticleNotification) {
        validateWebhookUrl(message.webhookUrl)
        try {
            val response = discordClient.post()
                .uri(message.webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(toDiscordPayload(message))
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(15))
            check(response?.statusCode?.is2xxSuccessful == true) { "Discord webhook delivery failed" }
        } catch (ex: WebClientResponseException.TooManyRequests) {
            val body = try {
                mapper.readTree(ex.responseBodyAsString)
            } catch (_: com.fasterxml.jackson.core.JsonProcessingException) {
                null
            }
            val headerSeconds = ex.headers.getFirst("Retry-After")?.toDoubleOrNull()
            val bodySeconds = body?.get("retry_after")?.asDouble()
            val retryAfterSeconds = maxOf(validDelay(headerSeconds), validDelay(bodySeconds))
            val seconds = if (retryAfterSeconds > 0) retryAfterSeconds else 1.0
            val global = ex.headers.getFirst("X-RateLimit-Global") == "true" ||
                ex.headers.getFirst("X-RateLimit-Scope") == "global" ||
                body?.get("global")?.asBoolean() == true
            throw WebhookRateLimited(ceil(seconds * 1000 * RETRY_DELAY_MULTIPLIER).toLong(), global)
        }
    }

    private fun validDelay(seconds: Double?): Double {
        if (seconds == null || !seconds.isFinite() || seconds <= 0) return 0.0
        return seconds
    }

    private fun toDiscordPayload(message: ArticleNotification): DiscordWebhookPayload {
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
