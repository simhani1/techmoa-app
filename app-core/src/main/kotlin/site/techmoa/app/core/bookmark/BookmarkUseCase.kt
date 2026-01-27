package site.techmoa.app.core.bookmark

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.core.article.ArticleUseCase
import site.techmoa.app.core.article.domain.Article
import site.techmoa.app.core.bookmark.domain.Bookmark
import site.techmoa.app.core.bookmark.port.BookmarkPort
import site.techmoa.app.core.member.MemberUseCase
import site.techmoa.app.core.member.domain.Member
import site.techmoa.app.storage.db.repository.BookmarkRepository

@Service
class BookmarkUseCase(
    private val memberUseCase: MemberUseCase,
    private val articleUseCase: ArticleUseCase,
    private val bookmarkPort: BookmarkPort,
    private val bookmarkRepository: BookmarkRepository
) {
    @Transactional(readOnly = true)
    fun findBy(memberId: Long): Bookmark {
        return bookmarkPort.findBy(memberId)
    }

    @Transactional
    fun favorite(memberId: Long, articleId: Long) {
        bookmarkRepository.findBy
        val bookmarkedArticle = bookmarkPort.findBy(memberId, articleId)
        if (bookmarkedArticle == null) {

        } else {
            bookmarkedArticle
        }
    }

    @Transactional
    fun remove(member: Member, article: Article) {
        bookmarkPort.remove(member, article)
    }
}