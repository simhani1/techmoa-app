package site.techmoa.batch.rss.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import site.techmoa.batch.rss.domain.model.Blog
import site.techmoa.batch.rss.domain.model.BlogStatus
import site.techmoa.batch.rss.port.BlogPort

@Repository(value = "RssBlogRepository")
class BlogRepository(
    private val jdbcTemplate: JdbcTemplate
) : BlogPort {

    override fun findAllBy(active: BlogStatus): List<Blog> {
        val sql = """
            SELECT
                blog_id,
                link,
                name,
                logo_url,
                rss_link
            FROM blog
            WHERE operation_status = ?
        """.trimIndent()

        return jdbcTemplate.query(sql, { rs, _ ->
            Blog(
                id = rs.getLong("blog_id"),
                link = rs.getString("link"),
                name = rs.getString("name"),
                logoUrl = rs.getString("logo_url"),
                rssLink = rs.getString("rss_link")
            )
        }, active.name)
    }
}
