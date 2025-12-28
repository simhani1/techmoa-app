package site.techmoa.app.storage.db.entity

import jakarta.persistence.*

@Entity
@Table(name = "blog")
class Blog(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id", nullable = false)
    var id: Long? = null,

    @Column(name = "link", nullable = false, length = 1000)
    val link: String,

    @Column(name = "logo_url", length = 1000)
    val logoUrl: String,

    @Column(name = "rss_link", nullable = false, length = 1000)
    val rssLink: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status", nullable = false, length = 20)
    private var operationStatus: OperationStatus = OperationStatus.ACTIVE
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

    fun delete() {
        this.operationStatus = OperationStatus.DELETED
    }

    fun pause() {
        this.operationStatus = OperationStatus.PAUSED
    }
}


