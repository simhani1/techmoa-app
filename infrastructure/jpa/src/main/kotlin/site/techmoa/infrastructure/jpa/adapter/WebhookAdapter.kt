package site.techmoa.infrastructure.jpa.adapter

import org.springframework.stereotype.Repository
import site.techmoa.application.port.WebhookPort
import site.techmoa.domain.model.Webhook
import site.techmoa.domain.model.WebhookPlatform
import site.techmoa.infrastructure.jpa.entity.WebhookEntity
import site.techmoa.infrastructure.jpa.repository.WebhookJpaRepository

@Repository
class WebhookAdapter(
    private val webhookRepository: WebhookJpaRepository,
) : WebhookPort {

    override fun existsBy(platform: WebhookPlatform, url: String): Boolean {
        return webhookRepository.existsByPlatformAndUrl(platform, url)
    }

    override fun save(webhook: Webhook): Webhook {
        return webhookRepository.save(WebhookEntity.from(webhook)).toDomain()
    }
}
