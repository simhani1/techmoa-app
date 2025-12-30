package site.techmoa.app.storage.db.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.techmoa.app.storage.db.entity.ArticleEntity

interface ArticleRepository : JpaRepository<ArticleEntity, Long> {
    fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean

    @Query(" select a from ArticleEntity a where (:cursor is null or a.pubDate < :cursor) order by a.pubDate desc")
    fun findPublishedAfter(@Param("cursor") cursor: Long?, toPageable: Pageable): List<ArticleEntity>
}
