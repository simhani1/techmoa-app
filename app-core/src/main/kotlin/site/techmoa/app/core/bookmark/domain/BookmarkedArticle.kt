package site.techmoa.app.core.bookmark.domain

class BookmarkedArticle(
    val articleId: Long,
    val bookmarkedAt: Long = System.currentTimeMillis()
) {
}
