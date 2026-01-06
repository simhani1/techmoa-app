package site.techmoa.app.article.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.techmoa.app.article.domain.Article
import site.techmoa.app.article.domain.ArticleContent
import site.techmoa.app.article.support.ArticleFinder
import site.techmoa.app.blog.domain.Blog
import site.techmoa.app.blog.support.BlogFinder
import site.techmoa.app.core.OffsetLimit
import site.techmoa.app.core.Page
import site.techmoa.app.storage.db.repository.ArticleRepository

@Service
class ArticleService(
    private val articleFinder: ArticleFinder,
    private val blogFinder: BlogFinder,
    private val articleRepository: ArticleRepository
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

    @Transactional
    fun increaseViewCount(articleId: Long) {
        val article =
            articleRepository.findByIdOrNull(articleId) ?: throw RuntimeException("Article with id $articleId not found")
        article.increaseViews()
    }
}
