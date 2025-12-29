package site.techmoa.app.storage.db.repository

import org.springframework.data.jpa.repository.JpaRepository
import site.techmoa.app.storage.db.entity.Article

interface ArticleRepository : JpaRepository<Article, Long> {
    fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean
}
