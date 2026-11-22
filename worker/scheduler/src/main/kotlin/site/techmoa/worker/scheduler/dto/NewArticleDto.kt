package site.techmoa.worker.scheduler.dto

data class NewArticleDto(
    val articleId: Long,
    val storedAt: java.time.LocalDateTime,
    val title: String,
    val link: String,
    val pubDate: Long,
    val blogId: Long,
    val blogName: String,
) {
}