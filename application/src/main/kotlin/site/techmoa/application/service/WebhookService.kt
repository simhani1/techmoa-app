package site.techmoa.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.techmoa.application.usecase.SaveWebhookUseCase
import site.techmoa.domain.model.Webhook
import site.techmoa.domain.model.WebhookPlatform

@Service
class WebhookService(
    private val saveWebhookUseCase: SaveWebhookUseCase,
) {

    @Transactional
    fun save(platform: WebhookPlatform, url: String): Webhook {
        val newWebhook = Webhook.of(
            platform = platform,
            url = url,
        )
        return saveWebhookUseCase.save(newWebhook)
    }
}
