package site.techmoa.app.core.bookmark.port

import site.techmoa.app.core.article.domain.Article
import site.techmoa.app.core.bookmark.domain.BookmarkedArticle
import site.techmoa.app.core.member.domain.Member

interface BookmarkPort {
    fun findBy(memberId: Long): BookmarkedArticle?
    fun save()
    fun remove(member: Member, article: Article)
}
