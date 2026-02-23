package site.techmoa.infrastructure.rest.adapter

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import site.techmoa.domain.event.EventTag
import site.techmoa.domain.event.NewArticlesEvents
import site.techmoa.domain.event.WebhookGatewayPort
import site.techmoa.domain.model.WebhookPlatform
import site.techmoa.infrastructure.rest.dto.DiscordEmbed
import site.techmoa.infrastructure.rest.dto.DiscordWebhookPayload
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class WebhookRestPublisher(
    webClient: WebClient.Builder
) : WebhookGatewayPort {

    private val log = LoggerFactory.getLogger(javaClass)
    private val discordClient = webClient.build()

    override fun publish(message: NewArticlesEvents.NewArticlesEvent) {
        if (message.tag != EventTag.NEW_ARTICLE) {
            return
        }
        when (message.webhook.platform) {
            WebhookPlatform.DISCORD -> publishDiscord(message)
            else -> log.warn("Unsupported webhook platform: ${message.webhook.platform}")
        }
    }

    private fun publishDiscord(message: NewArticlesEvents.NewArticlesEvent) {
        val payload = toDiscordPayload(message)
        val response = discordClient.post()
            .uri(message.webhook.url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .retrieve()
            .toBodilessEntity()
            .block()

        if (response == null || !response.statusCode.is2xxSuccessful) {
            log.error("Webhook publishing error: ${response.body}")
            throw IllegalStateException("Failed to publish webhook. webhookId=${message.webhook.id}, status=${response?.statusCode}")
        }

        log.info("Published discord webhook. webhookId=${message.webhook.id}, articleId=${message.article.id}")
    }

    private fun toDiscordPayload(event: NewArticlesEvents.NewArticlesEvent): DiscordWebhookPayload {
        val publishedAt = Instant.ofEpochMilli(event.article.pubDate)
            .atZone(ZoneId.of("Asia/Seoul"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val embed = DiscordEmbed(
            title = event.article.title,
            url = event.article.link,
            description = """
                발행일: $publishedAt
            """.trimIndent(),
        )

        return DiscordWebhookPayload(
            username = "TechMoaNews",
            embeds = listOf(embed)
        )
    }
}
