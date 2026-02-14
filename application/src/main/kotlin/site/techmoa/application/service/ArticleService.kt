package site.techmoa.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.techmoa.application.common.Page
import site.techmoa.application.dto.ArticleDto
import site.techmoa.application.port.ArticlePort
import site.techmoa.application.usecase.GetArticleUseCase
import site.techmoa.application.usecase.GetBlogUseCase

@Service
class ArticleService(
    private val articlePort: ArticlePort,
    private val getArticleUseCase: GetArticleUseCase,
    private val getBlogUseCase: GetBlogUseCase
) {

    @Transactional(readOnly = true)
    fun fetchPage(cursor: Long?, limit: Int): Page<ArticleDto> {
        val articlePage = getArticleUseCase.fetchPage(cursor, limit)

        val blogIds = articlePage.data.map { it.blogId }.distinct()
        val blogMap = getBlogUseCase.fetchBlogMapByIds(blogIds)

        val contents = articlePage.data.map { article -> ArticleDto(article, blogMap[article.blogId]!!) }

        return Page(
            data = contents,
            hasNext = articlePage.hasNext,
            nextCursor = articlePage.nextCursor
        )
    }

    @Transactional(readOnly = true)
    fun fetchOne(articleId: Long): ArticleDto {
        val article = getArticleUseCase.fetchOne(articleId)
        val blog = getBlogUseCase.fetchOne(article.blogId)
        return ArticleDto(article, blog)
    }

    @Transactional
    fun increaseViewCount(articleId: Long) {
        val article = getArticleUseCase.fetchOne(articleId)
        articlePort.increaseViews(articleId)
    }
}