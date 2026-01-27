package site.techmoa.app.storage.db.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp

@Entity
@Table(name = "bookmarked_article")
class BookmarkedArticleEntity(
    @EmbeddedId
    var id: BookmarkId = BookmarkId(),

    @CreationTimestamp
    @Column(name = "bookmarked_at", nullable = false)
    var bookmarkedAt: Long = System.currentTimeMillis(),
) : BaseEntity() {
}
