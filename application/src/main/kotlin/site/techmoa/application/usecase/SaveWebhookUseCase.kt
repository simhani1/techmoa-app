package site.techmoa.application.usecase

import org.springframework.stereotype.Component
import site.techmoa.application.port.WebhookPort
import site.techmoa.domain.exception.DuplicatedWebhookException
import site.techmoa.domain.model.Webhook

@Component
class SaveWebhookUseCase(
    private val webhookPort: WebhookPort,
) {

    fun save(webhook: Webhook): Webhook {
        if (webhookPort.existsBy(webhook.platform, webhook.url)) {
            throw DuplicatedWebhookException("Webhook already exists. platform=${webhook.platform}, url=${webhook.url}")
        }
        return webhookPort.save(webhook)
    }
}
