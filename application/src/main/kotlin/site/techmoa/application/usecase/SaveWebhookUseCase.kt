package site.techmoa.application.usecase

import org.springframework.stereotype.Component
import site.techmoa.application.port.WebhookPort
import site.techmoa.domain.exception.DuplicatedWebhookException
import site.techmoa.domain.model.Webhook
import site.techmoa.domain.model.WebhookPlatform

@Component
class SaveWebhookUseCase(
    private val webhookPort: WebhookPort,
) {

    fun save(platform: WebhookPlatform, url: String): Webhook {
        if (webhookPort.existsBy(platform, url)) {
            throw DuplicatedWebhookException("Webhook already exists. platform=$platform, url=$url")
        }
        return webhookPort.save(
            Webhook.of(
                platform = platform,
                url = url,
            )
        )
    }
}
