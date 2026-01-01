package site.techmoa.app.article.domain

data class Article(
    val id: Long,
    val blogId: Long,
    val title: String,
    val link: String,
    val pubDate: Long,
    val views: Int,
)
