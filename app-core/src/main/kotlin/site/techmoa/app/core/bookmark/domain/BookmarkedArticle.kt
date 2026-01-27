package site.techmoa.app.core.bookmark.domain

class BookmarkedArticle(
    val id: Long,
    val bookmarkedAt: Long,
    val status: BookmarkStatus
) {
}
