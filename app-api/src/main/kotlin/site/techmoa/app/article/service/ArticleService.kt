package site.techmoa.app.article.service

import org.springframework.stereotype.Service
import site.techmoa.app.article.domain.ArticleContent
import site.techmoa.app.article.support.ArticleFinder
import site.techmoa.app.blog.support.BlogFinder
import site.techmoa.app.core.response.OffsetLimit
import site.techmoa.app.core.response.Page

@Service
class ArticleService(
    private val articleFinder: ArticleFinder,
    private val blogFinder: BlogFinder
) {
    fun getArticles(cursor: Long?, limit: Int): Page<ArticleContent> {
        val articles = articleFinder.findPublishedAfter(cursor, OffsetLimit(limit = limit))
        val contents = articles.map { article ->
            val blog = blogFinder.findById(article.blogId)
            ArticleContent(article, blog)
        }
        val hasNext = articles.size == limit
        val nextCursor = articles.lastOrNull()?.pubDate
        return Page(
            data = contents,
            hasNext = hasNext,
            nextCursor = nextCursor
        )
    }
}