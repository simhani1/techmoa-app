package site.techmoa.infrastructure.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.techmoa.domain.model.BlogStatus
import site.techmoa.infrastructure.jpa.entity.BlogEntity

interface BlogJpaRepository : JpaRepository<BlogEntity, Long> {

    @Query("select b from BlogEntity b where b._status = :status")
    fun findAllStatus(@Param("status") status: BlogStatus): List<BlogEntity>
}