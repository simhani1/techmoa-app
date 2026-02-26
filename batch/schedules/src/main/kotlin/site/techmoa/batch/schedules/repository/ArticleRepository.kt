package site.techmoa.batch.schedules.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import site.techmoa.batch.schedules.dto.NewArticleDto
import site.techmoa.domain.model.Article

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

    fun findByBlogIdAndGuid(blogId: Long, guid: String): Article? {
        val sql = """
            SELECT article_id, blog_id, title, link, guid, pub_date, views
            FROM article
            WHERE blog_id = ?
                AND guid = ?
            LIMIT 1
        """.trimIndent()

        val result = jdbcTemplate.query(
            sql,
            RowMapper { rs, _ ->
                Article(
                    id = rs.getLong("article_id"),
                    blogId = rs.getLong("blog_id"),
                    title = rs.getString("title"),
                    link = rs.getString("link"),
                    guid = rs.getString("guid"),
                    pubDate = rs.getLong("pub_date"),
                    views = rs.getInt("views")
                )
            },
            blogId,
            guid
        )

        return result.firstOrNull()
    }
}
