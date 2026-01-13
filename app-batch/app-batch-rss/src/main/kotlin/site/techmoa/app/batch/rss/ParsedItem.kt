package site.techmoa.app.batch.rss

sealed class ParsedItem {

    data class Valid(
        val title: String,
        val link: String,
        val guid: String,
        val pubDate: Long
    ) : ParsedItem()

    data object Invalid : ParsedItem()
}