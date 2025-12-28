package site.techmoa.app.storage.db.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "article",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_article_blog_guid",
            columnNames = ["blog_id", "guid"]
        )
    ]
)
class Article(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id", nullable = false)
    var id: Long? = null,

    @Column(name = "blog_id", nullable = false)
    val blogId: Long,

    @Column(name = "title", nullable = false, length = 500)
    val title: String,

    @Column(name = "link", nullable = false, length = 600)
    val link: String,

    @Column(name = "guid", nullable = false, length = 600)
    val guid: String,

    @Column(name = "pub_date", nullable = false)
    val pubDate: LocalDateTime,

    @Column(name = "views", nullable = false)
    private var views: Int = 0,
) : BaseEntity() {

    companion object {
        fun of(
            blogId: Long,
            title: String,
            link: String,
            guid: String,
            pubDate: LocalDateTime,
        ): Article {
            return Article(null, blogId, title, link, guid, pubDate)
        }
    }

    fun increaseViews(amount: Int = 1) {
        this.views += amount
    }
}