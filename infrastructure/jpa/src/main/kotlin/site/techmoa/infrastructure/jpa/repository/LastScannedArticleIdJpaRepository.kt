package site.techmoa.infrastructure.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import site.techmoa.infrastructure.jpa.entity.LastScannedArticleIdEntity

interface LastScannedArticleIdJpaRepository : JpaRepository<LastScannedArticleIdEntity, String>
