package site.techmoa.batch.schedules.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import site.techmoa.domain.model.Article

@Repository(value = "SchedulesArticleRepository")
class ArticleRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun findByIdGreaterThan(articleId: Long): List<Article> {
        val sql = """
            SELECT article_id, blog_id, title, link, guid, pub_date, views
            FROM article
            WHERE article_id > ?
        """.trimIndent()

        val rowMapper = RowMapper { rs, _ ->
            Article(
                id = rs.getLong("article_id"),
                blogId = rs.getLong("blog_id"),
                title = rs.getString("title"),
                link = rs.getString("link"),
                guid = rs.getString("guid"),
                pubDate = rs.getLong("pub_date"),
                views = rs.getInt("views")
            )
        }

        return jdbcTemplate.query(
            sql,
            rowMapper,
            articleId
        )
    }
}
