package site.techmoa.app.common.blog

data class Blog(
    val id: Long,
    val link: String,
    val name: String,
    val logoUrl: String,
    val rssLink: String,
) {
}