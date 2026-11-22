package site.techmoa.domain.event

import site.techmoa.domain.model.WebhookPlatform

import site.techmoa.domain.exception.InvalidWebhookPlatformException

class WebhookRateLimited(val delayMillis: Long, val global: Boolean) : RuntimeException()

interface WebhookSender {
    val platform: WebhookPlatform
    fun validateWebhookUrl(url: String)
    fun publish(message: ArticleNotification)
}

fun List<WebhookSender>.forPlatform(platform: WebhookPlatform): WebhookSender =
    singleOrNull { it.platform == platform }
        ?: throw InvalidWebhookPlatformException("Unsupported webhook platform: $platform")
