package site.techmoa.domain.event

import site.techmoa.domain.event.ArticleNotification

class WebhookRateLimited(val delayMillis: Long, val global: Boolean) : RuntimeException()

interface WebhookSender {
    fun publish(message: ArticleNotification)
}