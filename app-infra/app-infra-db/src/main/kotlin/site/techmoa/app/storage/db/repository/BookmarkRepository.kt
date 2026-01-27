package site.techmoa.app.storage.db.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.techmoa.app.storage.db.entity.BookmarkId
import site.techmoa.app.storage.db.entity.BookmarkedArticleEntity

interface BookmarkRepository : JpaRepository<BookmarkedArticleEntity, BookmarkId> {
    @Query("SELECT e FROM BookmarkedArticleEntity e WHERE e.id.memberId = :memberId")
    fun findByMemberId(@Param("memberId") id: Long): List<BookmarkedArticleEntity>
}
