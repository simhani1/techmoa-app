package site.techmoa.infrastructure.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import site.techmoa.domain.event.OutboxStatus
import site.techmoa.infrastructure.jpa.entity.OutboxEntity

interface OutboxJpaRepository : JpaRepository<OutboxEntity, Long> {
    fun existsByIdempotencyKey(idempotencyKey: String): Boolean
    fun findTop100ByStatusOrderByIdAsc(status: OutboxStatus): List<OutboxEntity>
}
