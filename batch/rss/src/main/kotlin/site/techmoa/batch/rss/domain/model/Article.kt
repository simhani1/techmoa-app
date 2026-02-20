package site.techmoa.batch.rss.domain.model

data class Article(
    val id: Long,
    val blogId: Long,
    val title: String,
    val link: String,
    val guid: String,
    val pubDate: Long,
    var views: Int,
) {
    companion object {
        fun of(
            id: Long = 0,
            blogId: Long,
            title: String,
            link: String,
            guid: String,
            pubDate: Long,
            views: Int = 0
        ): Article {
            return Article(
                id = id,
                blogId = blogId,
                title = title,
                link = link,
                guid = guid,
                pubDate = pubDate,
                views = views
            )
        }
    }
}
