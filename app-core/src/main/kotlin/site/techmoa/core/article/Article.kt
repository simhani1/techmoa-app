package site.techmoa.core.article

data class Article(
    val id: Long,
    val blogId: Long,
    val title: String,
    val link: String,
    val pubDate: Long,
    var views: Int,
) {
    fun increaseViews(amount: Int = 1) {
        views += amount
    }
}