package site.techmoa.app.article.controller

import org.springframework.web.bind.annotation.*
import site.techmoa.app.article.domain.ArticleContent
import site.techmoa.app.article.service.ArticleService
import site.techmoa.app.core.Page
import site.techmoa.app.core.response.ApiResponse

@RestController
class ArticleController(
    private val articleService: ArticleService,
) {

    companion object {
        const val PAGE_LIMIT = 20
    }

    @GetMapping("/v1/articles")
    fun getArticles(
        @RequestParam("cursor", required = false) cursor: Long?,
    ): ApiResponse<Page<ArticleContent>> {
        val articles = articleService.getArticles(cursor, PAGE_LIMIT)
        return ApiResponse.success(articles)
    }

    @PostMapping("/v1/articles/{articleId}/views")
    fun increaseViewCount(
        @PathVariable articleId: Long
    ): ApiResponse<Any> {
        articleService.increaseViewCount(articleId)
        return ApiResponse.success()
    }
}