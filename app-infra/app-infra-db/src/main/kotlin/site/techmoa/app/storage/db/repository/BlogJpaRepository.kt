package site.techmoa.app.storage.db.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.techmoa.app.common.blog.BlogStatus
import site.techmoa.app.storage.db.entity.BlogEntity

interface BlogJpaRepository : JpaRepository<BlogEntity, Long> {

    @Query("select b from BlogEntity b where b._status = :status")
    fun findAllStatus(@Param("status") status: BlogStatus): List<BlogEntity>
}