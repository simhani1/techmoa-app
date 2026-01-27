package site.techmoa.app.storage.db.adapter

import org.springframework.boot.autoconfigure.web.embedded.TomcatVirtualThreadsWebServerFactoryCustomizer
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import site.techmoa.app.core.article.domain.Article
import site.techmoa.app.core.bookmark.domain.Bookmark
import site.techmoa.app.core.bookmark.domain.BookmarkedArticle
import site.techmoa.app.core.bookmark.port.BookmarkPort
import site.techmoa.app.core.member.domain.Member
import site.techmoa.app.storage.db.entity.BookmarkId
import site.techmoa.app.storage.db.entity.BookmarkedArticleEntity
import site.techmoa.app.storage.db.repository.BookmarkRepository

@Repository
class BookmarkAdapter(
    private val bookmarkRepository: BookmarkRepository,
    private val tomcatVirtualThreadsWebServerFactoryCustomizer: TomcatVirtualThreadsWebServerFactoryCustomizer
) : BookmarkPort {
    override fun findBy(memberId: Long): Bookmark {
        val bookmarkedArticles = bookmarkRepository.findByMemberId(member.id)
            .map { BookmarkedArticle(it.id.articleId, it.bookmarkedAt) }
            .toMutableSet()
        return Bookmark(member, bookmarkedArticles)
    }

    override fun save(member: Member, article: Article) {
        val entity = BookmarkedArticleEntity(BookmarkId(member.id, article.id))
        bookmarkRepository.save(entity)
    }

    override fun remove(member: Member, article: Article) {
        val entity = bookmarkRepository.findByIdOrNull(BookmarkId(member.id, article.id))
            ?:
        bookmarkRepository.delete(entity)
    }
}
