package site.techmoa.batch.schedules.event

data class NewArticleNotification(
    val webhookUrl: String,
    val idempotencyKey: String,
    val blogName: String,
    val articleName: String,
    val articleUrl: String
)
