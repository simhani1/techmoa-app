package site.techmoa.app.storage.db.entity

import jakarta.persistence.*

@Entity
@Table(name = "blog")
class Blog(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id", nullable = false)
    var id: Long? = null,

    @Column(name = "link", nullable = false, length = 600)
    val link: String,

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
            logoUrl: String,
            rssLink: String
        ): Blog {
            return Blog(null, link, logoUrl, rssLink)
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
        if (other !is Blog) return false
        if (id == null || other.id == null) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

