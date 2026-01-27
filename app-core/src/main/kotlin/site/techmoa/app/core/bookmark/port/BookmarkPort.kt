package site.techmoa.app.core.bookmark.port

import site.techmoa.app.core.bookmark.domain.Bookmark
import site.techmoa.app.core.bookmark.domain.BookmarkedArticle
import site.techmoa.app.core.member.domain.Member

interface BookmarkPort {
    fun findBy(memberId: Member): Bookmark
    fun save(article: BookmarkedArticle)
    fun remove(bookmark: Bookmark)
}
