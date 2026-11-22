package site.techmoa.domain.event

data class ArticleNotification(
    val articleId: Long,
    val title: String,
    val link: String,
    val pubDate: Long,
    val blogName: String,
    val webhookUrl: String,
)
