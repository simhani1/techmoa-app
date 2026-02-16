package site.techmoa.presentation.controller.request

import site.techmoa.domain.exception.InvalidWebhookPlatformException
import site.techmoa.domain.exception.InvalidWebhookUrlException
import site.techmoa.domain.model.WebhookPlatform

class SaveWebhookRequest(
    val platform: String,
    val url: String,
) {
    fun toPlatform(): WebhookPlatform {
        return runCatching {
            WebhookPlatform.valueOf(platform.trim().uppercase())
        }.getOrElse {
            throw InvalidWebhookPlatformException("Unsupported webhook platform: $platform")
        }
    }

    fun normalizedUrl(): String {
        val normalized = url.trim()
        if (normalized.isBlank()) {
            throw InvalidWebhookUrlException("Webhook url must not be blank.")
        }
        return normalized
    }
}
