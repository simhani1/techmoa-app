package site.techmoa.app.storage.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class BookmarkId(
    @Column(name = "member_id", nullable = false)
    var memberId: Long = 0L,

    @Column(name = "article_id", nullable = false)
    var articleId: Long = 0L,
) : Serializable
