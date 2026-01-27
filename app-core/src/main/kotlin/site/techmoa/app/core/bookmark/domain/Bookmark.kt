package site.techmoa.app.core.bookmark.domain

import site.techmoa.app.core.member.domain.Member

class Bookmark(
    private val member: Member,
    private val articles: MutableSet<BookmarkedArticle> = mutableSetOf()
) {
    fun add(articleId: Long) {
        val article = BookmarkedArticle(articleId)
        articles.add(article)
    }

    fun remove(articleId: Long) {
        val article = BookmarkedArticle(articleId)
        articles.remove(article)
    }

    fun getArticles(): Set<BookmarkedArticle> {
        return articles
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bookmark

        return articles == other.articles
    }

    override fun hashCode(): Int {
        return articles.hashCode()
    }
}