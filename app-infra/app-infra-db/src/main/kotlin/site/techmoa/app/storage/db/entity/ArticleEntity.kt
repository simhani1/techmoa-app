package site.techmoa.app.storage.db.entity

import jakarta.persistence.*

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
class ArticleEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id", nullable = false)
    var id: Long = 0L,

    @Column(name = "blog_id", nullable = false)
    val blogId: Long,

    @Column(name = "title", nullable = false, length = 500)
    val title: String,

    @Column(name = "link", nullable = false, length = 600)
    val link: String,

    @Column(name = "guid", nullable = false, length = 600)
    val guid: String,

    @Column(name = "pub_date", nullable = false)
    val pubDate: Long,

    @Column(name = "views", nullable = false)
    private var _views: Int = 0,
) : BaseEntity() {

    companion object {
        fun of(
            blogId: Long,
            title: String,
            link: String,
            guid: String,
            pubDate: Long,
        ): ArticleEntity {
            return ArticleEntity(0L, blogId, title, link, guid, pubDate)
        }
    }

    fun getViews(): Int {
        return _views
    }

    fun increaseViews(amount: Int = 1) {
        _views += amount
    }
}