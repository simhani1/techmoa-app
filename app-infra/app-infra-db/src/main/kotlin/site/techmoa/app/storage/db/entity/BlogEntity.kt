package site.techmoa.app.storage.db.entity

import jakarta.persistence.*
import site.techmoa.app.common.blog.BlogStatus

@Entity
@Table(name = "blog")
class BlogEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id", nullable = false)
    var id: Long = 0L,

    @Column(name = "link", nullable = false, length = 600)
    val link: String,

    @Column(name = "name", nullable = false, length = 20)
    val name: String,

    @Column(name = "logo_url", length = 600)
    val logoUrl: String,

    @Column(name = "rss_link", nullable = false, length = 600)
    val rssLink: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status", nullable = false, length = 20)
    private var _status: BlogStatus = BlogStatus.ACTIVE,
) : BaseEntity() {

    companion object {
        fun of(
            link: String,
            name: String,
            logoUrl: String,
            rssLink: String
        ): BlogEntity {
            return BlogEntity(
                link = link,
                name = name,
                logoUrl = logoUrl,
                rssLink = rssLink
            )
        }
    }

    val status: BlogStatus
        get() = _status

    fun delete() {
        _status = BlogStatus.DELETED
    }

    fun pause() {
        _status = BlogStatus.PAUSED
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlogEntity) return false
        if (id == 0L || other.id == 0L) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

