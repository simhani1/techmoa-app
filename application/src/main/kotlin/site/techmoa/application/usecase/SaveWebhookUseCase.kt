package site.techmoa.application.usecase

import site.techmoa.domain.event.WebhookSender
import site.techmoa.domain.event.forPlatform

import org.springframework.stereotype.Component
import site.techmoa.application.port.WebhookPort
import site.techmoa.domain.exception.DuplicatedWebhookException
import site.techmoa.domain.model.Webhook

@Component
class SaveWebhookUseCase(
    private val webhookPort: WebhookPort,
    private val senders: List<WebhookSender>,
) {

    fun save(webhook: Webhook): Webhook {
        senders.forPlatform(webhook.platform).validateWebhookUrl(webhook.url)
        if (webhookPort.existsBy(webhook.platform, webhook.url)) {
            throw DuplicatedWebhookException("Webhook already exists. platform=${webhook.platform}")
        }
        return webhookPort.save(webhook)
    }
}
