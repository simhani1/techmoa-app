package site.techmoa.batch.rss.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import site.techmoa.batch.rss.domain.model.Article
import site.techmoa.batch.rss.port.ArticlePort
import java.sql.Timestamp
import java.time.Instant

@Repository(value = "RssArticleRepository")
class ArticleRepository(
    private val jdbcTemplate: JdbcTemplate
) : ArticlePort {

    override fun saveAllIgnoringDuplicates(articles: List<Article>) {
        if (articles.isEmpty()) return

        val now = Timestamp.from(Instant.now())
        val sql = """
            INSERT IGNORE 
            INTO article (blog_id, title, link, guid, pub_date, views, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
        """.trimIndent()

        val batchArgs = articles.map { article ->
            arrayOf(
                article.blogId,
                article.title,
                article.link,
                article.guid,
                article.pubDate,
                article.views,
                now,
                now
            )
        }

        jdbcTemplate.batchUpdate(sql, batchArgs)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun existsByBlogIdAndGuid(blogId: Long, guid: String): Boolean {
        val sql = """
            SELECT EXISTS (
                SELECT 1
                FROM article
                WHERE blog_id = ? AND guid = ?
            )
        """.trimIndent()

        return jdbcTemplate.queryForObject(
            sql,
            Int::class.java,
            blogId,
            guid
        ) > 0
    }
}
