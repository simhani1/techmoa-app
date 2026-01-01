package site.techmoa.app.blog.domain

data class BlogProfile(
    val id: Long = 0,
    val link: String,
    val name: String,
    val logoUrl: String,
    val rssLink: String
)
