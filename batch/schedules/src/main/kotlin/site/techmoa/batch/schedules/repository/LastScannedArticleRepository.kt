package site.techmoa.batch.schedules.repository

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import site.techmoa.batch.schedules.dto.LastScannedArticleDto
import java.sql.Timestamp
import java.time.Instant

@Repository(value = "SchedulesLastScannedArticleRepository")
class LastScannedArticleRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun findBy(jobName: String): LastScannedArticleDto? {
        val sql = """
            SELECT last_scanned_id
            FROM scheduler_scan_support
            WHERE job_name = ?
        """.trimIndent()

        return try {
            val lastScannedId = jdbcTemplate.queryForObject(
                sql,
                Long::class.java,
                jobName
            )
            lastScannedId?.let { LastScannedArticleDto(it) }
        } catch (_: EmptyResultDataAccessException) {
            null
        }
    }

    fun sync(publishNotificationJob: String, id: Long) {
        val now = Timestamp.from(Instant.now())
        val sql = """
            INSERT INTO scheduler_scan_support (job_name, last_scanned_id, created_at, updated_at)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                last_scanned_id = VALUES(last_scanned_id),
                updated_at = VALUES(updated_at)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            publishNotificationJob,
            id,
            now,
            now
        )
    }
}
