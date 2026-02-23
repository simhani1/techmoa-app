package site.techmoa.infrastructure.jpa.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.techmoa.infrastructure.jpa.entity.ArticleEntity

interface ArticleJpaRepository : JpaRepository<ArticleEntity, Long> {
    fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean

    @Query(" select a from ArticleEntity a where (:cursor is null or a.pubDate < :cursor) order by a.pubDate desc")
    fun findPublishedAfter(@Param("cursor") cursor: Long?, pageable: Pageable): List<ArticleEntity>

    @Modifying
    @Query("update ArticleEntity a set a._views = a._views + 1 where a.id = :id")
    fun increaseViews(@Param("id") id: Long): Int

    @Query("select a from ArticleEntity a where a.id > :id")
    fun findByIdGreaterThan(articleId: Long): List<ArticleEntity>
}
