package site.techmoa.app.article.controller.v1

import org.springframework.web.bind.annotation.*
import site.techmoa.app.core.article.ArticleContent
import site.techmoa.app.core.article.ArticleUseCase
import site.techmoa.app.core.common.Page
import site.techmoa.app.core.response.ApiResponse

@RestController
class ArticleController(
    private val articleUseCase: ArticleUseCase,
) {

    companion object {
        const val PAGE_LIMIT = 20
    }

    @GetMapping("/v1/articles")
    fun getArticles(
        @RequestParam("cursor", required = false) cursor: Long?,
    ): ApiResponse<Page<ArticleContent>> {
        val articles = articleUseCase.getArticles(cursor, PAGE_LIMIT)
        return ApiResponse.success(articles)
    }

    @PostMapping("/v1/articles/{articleId}/views")
    fun increaseViewCount(
        @PathVariable articleId: Long
    ): ApiResponse<Any> {
        articleUseCase.increaseViewCount(articleId)
        return ApiResponse.success()
    }
}