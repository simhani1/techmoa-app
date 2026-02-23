package site.techmoa.infrastructure.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import site.techmoa.domain.model.WebhookPlatform
import site.techmoa.domain.model.WebhookValidity
import site.techmoa.infrastructure.jpa.entity.WebhookEntity

interface WebhookJpaRepository : JpaRepository<WebhookEntity, Long> {
    fun existsByPlatformAndUrl(platform: WebhookPlatform, url: String): Boolean
    fun findAllByValidity(valid: WebhookValidity): List<WebhookEntity>
}
