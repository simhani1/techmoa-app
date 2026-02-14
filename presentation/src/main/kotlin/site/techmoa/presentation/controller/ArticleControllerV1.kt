package site.techmoa.presentation.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import site.techmoa.application.common.Page
import site.techmoa.application.dto.ArticleDto
import site.techmoa.application.service.ArticleService
import site.techmoa.presentation.common.template.ApiResponse

@RequestMapping("/v1/articles")
@RestController
class ArticleControllerV1(
    private val articleService: ArticleService,
) {

    companion object {
        const val PAGE_LIMIT = 20
    }

    @GetMapping()
    fun getArticles(
        @RequestParam("cursor", required = false) cursor: Long?,
    ): ResponseEntity<ApiResponse<Page<ArticleDto>>> {
        val dataPage = articleService.fetchPage(cursor, PAGE_LIMIT)
        return ResponseEntity.ok(ApiResponse.success(dataPage))
    }

    @GetMapping("/{articleId}")
    fun getArticle(
        @PathVariable articleId: Long
    ): ResponseEntity<ApiResponse<ArticleDto>> {
        val data = articleService.fetchOne(articleId)
        return ResponseEntity.ok(ApiResponse.success(data))
    }

    @PostMapping("/{articleId}")
    fun increaseViewCount(
        @PathVariable articleId: Long
    ): ResponseEntity<ApiResponse<Any>> {
        articleService.increaseViewCount(articleId)
        return ResponseEntity.ok(ApiResponse.success())
    }
}