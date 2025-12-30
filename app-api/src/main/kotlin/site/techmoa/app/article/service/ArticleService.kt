package site.techmoa.app.article.service

import org.springframework.stereotype.Service
import site.techmoa.app.article.domain.ArticleContent
import site.techmoa.app.article.support.ArticleFinder
import site.techmoa.app.blog.support.BlogFinder
import site.techmoa.app.core.OffsetLimit
import site.techmoa.app.core.Page

@Service
class ArticleService(
    private val articleFinder: ArticleFinder,
    private val blogFinder: BlogFinder
) {
    fun getArticles(cursor: Long?, limit: Int): Page<ArticleContent> {
        val fetchArticles = articleFinder.findPublishedAfter(cursor, OffsetLimit(limit = limit))
        val hasNext = fetchArticles.size > limit
        val articles = fetchArticles.take(limit)
        val contents = articles.map { article ->
            val blog = blogFinder.findById(article.blogId)
            ArticleContent(article, blog)
        }
        val nextCursor = if (hasNext) articles.last().pubDate else null
        return Page(
            data = contents,
            hasNext = hasNext,
            nextCursor = nextCursor
        )
    }
}
