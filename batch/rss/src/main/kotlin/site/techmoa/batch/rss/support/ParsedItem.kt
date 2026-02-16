package site.techmoa.batch.rss.support

sealed class ParsedItem {

    data class Valid(
        val title: String,
        val link: String,
        val guid: String,
        val pubDate: Long
    ) : ParsedItem()

    data object Invalid : ParsedItem()
}