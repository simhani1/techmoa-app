package site.techmoa.infrastructure.rest.dto

data class DiscordWebhookPayload(
    val username: String? = null,
    val avatarUrl: String? = null,
    val embeds: List<DiscordEmbed>
)

data class DiscordEmbed(
    val title: String,
    val description: String,
    val url: String,
)
