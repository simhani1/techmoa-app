package site.techmoa.domain.model

data class Webhook(
    val id: Long,
    val url: String,
    val validity: WebhookValidity,
    val platform: WebhookPlatform,
) {
    companion object {
        fun of(
            id: Long = 0,
            url: String,
            validity: WebhookValidity = WebhookValidity.INVALID,
            platform: WebhookPlatform
        ): Webhook {
            return Webhook(
                id = id,
                url = url,
                validity = validity,
                platform = platform,
            )
        }
    }
}
