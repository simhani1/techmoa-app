package site.techmoa.infrastructure.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import site.techmoa.infrastructure.jpa.entity.ArticleCreatedOutboxEntity

interface ArticleCreatedOutboxJpaRepository : JpaRepository<ArticleCreatedOutboxEntity, Long> {
}
