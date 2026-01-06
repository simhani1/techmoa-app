package site.techmoa.core.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.techmoa.core.blog.BlogFinder
import site.techmoa.core.common.OffsetLimit
import site.techmoa.core.common.Page

@Service
class ArticleUseCase(
    private val articleFinder: ArticleFinder,
    private val blogFinder: BlogFinder
){
    @Transactional(readOnly = true)
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
}