package site.techmoa.domain.model

data class Webhook(
    val id: Long,
    val url: String,
    val owner: Member,
    val validity: WebhookValidity,
    val platform: WebhookPlatform,
) {
    companion object {
        fun of(
            id: Long = 0,
            url: String,
            owner: Member,
            validity: WebhookValidity = WebhookValidity.VALID,
            platform: WebhookPlatform = WebhookPlatform.DISCORD,
        ): Webhook {
            return Webhook(
                id = id,
                url = url,
                owner = owner,
                validity = validity,
                platform = platform,
            )
        }
    }
}
