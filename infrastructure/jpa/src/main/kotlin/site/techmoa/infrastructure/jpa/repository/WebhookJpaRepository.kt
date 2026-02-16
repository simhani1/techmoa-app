package site.techmoa.infrastructure.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import site.techmoa.infrastructure.jpa.entity.WebhookEntity

interface WebhookJpaRepository : JpaRepository<WebhookEntity, Long> {
}
