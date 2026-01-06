package site.techmoa.app.article.service

import org.springframework.stereotype.Service
import site.techmoa.app.article.domain.Article
import site.techmoa.app.article.domain.ArticleContent
import site.techmoa.app.article.support.ArticleFinder
import site.techmoa.app.article.support.ArticleHandler
import site.techmoa.app.blog.domain.Blog
import site.techmoa.app.blog.support.BlogFinder
import site.techmoa.app.core.OffsetLimit
import site.techmoa.app.core.Page

@Service
class ArticleService(
    private val articleFinder: ArticleFinder,
    private val articleHandler: ArticleHandler,
    private val blogFinder: BlogFinder,
) {
    fun getArticles(cursor: Long?, limit: Int): Page<ArticleContent> {
        val fetchArticles = articleFinder.findPublishedAfter(cursor, OffsetLimit(limit = limit))
        val articles = fetchArticles.take(limit)

        val blogIds = articles.map { it.blogId }.distinct()
        val blogMap = blogFinder.findByIds(blogIds).associateBy { it.id }

        val contents = articles.map { article -> toContent(blogMap, article) }
        val hasNext = fetchArticles.size > limit
        val nextCursor = if (hasNext) articles.lastOrNull()?.pubDate else null

        return Page(
            data = contents,
            hasNext = hasNext,
            nextCursor = nextCursor
        )
    }

    private fun toContent(blogMap: Map<Long, Blog>, article: Article): ArticleContent {
        val blog = blogMap[article.blogId] ?: throw RuntimeException("Blog with id ${article.blogId} not found")
        return ArticleContent(article, blog)
    }

    fun increaseViewCount(articleId: Long) {
        articleHandler.increaseViewCount(articleId)
    }
}
