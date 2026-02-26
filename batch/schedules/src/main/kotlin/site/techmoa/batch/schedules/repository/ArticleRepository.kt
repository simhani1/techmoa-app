package site.techmoa.batch.schedules.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import site.techmoa.batch.schedules.dto.NewArticleDto

@Repository(value = "SchedulesArticleRepository")
class ArticleRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun findByIdGreaterThan(articleId: Long): List<NewArticleDto> {
        val sql = """
            SELECT a.article_id, a.blog_id, a.title, a.link, a.pub_date, b.name
            FROM article a
            LEFT JOIN blog b on b.blog_id = a.blog_id
            WHERE a.article_id > ?;
        """.trimIndent()

        return jdbcTemplate.query(
            sql,
            RowMapper { rs, _ ->
                NewArticleDto(
                    articleId = rs.getLong("article_id"),
                    title = rs.getString("title"),
                    link = rs.getString("link"),
                    pubDate = rs.getLong("pub_date"),
                    blogId = rs.getLong("blog_id"),
                    blogName = rs.getString("name"),
                )
            },
            articleId
        )
    }
}
