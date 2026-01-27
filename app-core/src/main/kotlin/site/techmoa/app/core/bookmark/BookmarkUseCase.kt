package site.techmoa.app.core.bookmark

import org.springframework.stereotype.Service
import site.techmoa.app.core.bookmark.domain.Bookmark
import site.techmoa.app.core.bookmark.domain.BookmarkedArticle
import site.techmoa.app.core.bookmark.port.BookmarkPort
import site.techmoa.app.core.member.domain.Member

@Service
class BookmarkUseCase(
    private val bookmarkPort: BookmarkPort
) {
    fun findBy(member: Member): Bookmark {
        return bookmarkPort.findBy(member)
    }

    fun add(member: Member, articleId: Long) {
        val newArticle = BookmarkedArticle(articleId)
        bookmarkPort.save(newArticle)
    }

    fun remove(bookmark: Bookmark) {
        bookmarkPort.remove(bookmark)
    }
}