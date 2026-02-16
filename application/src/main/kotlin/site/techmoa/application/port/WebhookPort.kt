package site.techmoa.application.port

import site.techmoa.domain.model.Webhook
import site.techmoa.domain.model.WebhookPlatform

interface WebhookPort {
    fun existsBy(platform: WebhookPlatform, url: String): Boolean
    fun save(webhook: Webhook): Webhook
}
