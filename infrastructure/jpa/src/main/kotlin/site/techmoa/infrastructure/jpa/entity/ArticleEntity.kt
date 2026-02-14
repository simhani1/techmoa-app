package site.techmoa.infrastructure.jpa.entity

import jakarta.persistence.*
import site.techmoa.domain.model.Article

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
        fun from(
            article: Article
        ): ArticleEntity {
            return ArticleEntity(
                id = 0L,
                blogId = article.blogId,
                title = article.title,
                link = article.link,
                guid = article.guid,
                pubDate = article.pubDate
            )
        }
    }

    fun getViews(): Int {
        return _views
    }

    fun increaseViews(amount: Int = 1) {
        _views += amount
    }
}
