package site.techmoa.app.common.article

import org.springframework.stereotype.Service
import site.techmoa.app.common.blog.BlogFinder
import site.techmoa.app.common.common.OffsetLimit
import site.techmoa.app.common.common.Page

@Service
class ArticleUseCase(
    private val articleFinder: ArticleFinder,
    private val articleViewHandler: ArticleViewHandler,
    private val blogFinder: BlogFinder
) {
    fun getArticles(cursor: Long?, limit: Int): Page<ArticleContent> {
        val fetchArticles = articleFinder.findPublishedAfter(cursor, OffsetLimit(limit = limit))
        val articles = fetchArticles.take(limit)

        val blogIds = articles.map { it.blogId }.distinct()
        val blogMap = blogFinder.findAll(blogIds).associateBy { it.id }

        val contents = articles.map { article -> ArticleContent(article, blogMap[article.blogId]!!) }
        val hasNext = fetchArticles.size > limit
        val nextCursor = if (hasNext) articles.lastOrNull()?.pubDate else null

        return Page(
            data = contents,
            hasNext = hasNext,
            nextCursor = nextCursor
        )
    }

    fun increaseViewCount(articleId: Long) {
        articleViewHandler.increaseViews(articleId)
    }

    fun getArticle(articleId: Long): Article {
        return articleFinder.findByIdOrNull(articleId)
    }
}