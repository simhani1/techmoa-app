package site.techmoa.app.storage.db.adapter

import org.springframework.stereotype.Repository
import site.techmoa.app.core.bookmark.domain.Bookmark
import site.techmoa.app.core.bookmark.domain.BookmarkedArticle
import site.techmoa.app.core.bookmark.port.BookmarkPort
import site.techmoa.app.core.member.domain.Member
import site.techmoa.app.storage.db.repository.BookmarkRepository

@Repository
class BookmarkAdapter(
    private val bookmarkRepository: BookmarkRepository
) : BookmarkPort {
    override fun findBy(member: Member): Bookmark {
        val bookmarkedArticles = bookmarkRepository.findByMemberId(member.id)
            .map { BookmarkedArticle(it.id.articleId, it.bookmarkedAt) }
            .toMutableSet()
        return Bookmark(member, bookmarkedArticles)
    }

    override fun save(article: BookmarkedArticle) {
        TODO("Not yet implemented")
    }

    override fun remove(bookmark: Bookmark) {
        TODO("Not yet implemented")
    }
}