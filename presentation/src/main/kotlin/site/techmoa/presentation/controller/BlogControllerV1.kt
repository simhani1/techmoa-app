package site.techmoa.presentation.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import site.techmoa.application.service.BlogService
import site.techmoa.domain.model.Blog
import site.techmoa.presentation.common.template.ApiResponse

@RequestMapping("/v1/blogs")
@RestController
class BlogControllerV1(
    private val blogService: BlogService,
) {

    @GetMapping()
    fun getAllBlogs(): ApiResponse<List<Blog>> {
        val blogs = blogService.getAllBlogs()
        return ApiResponse.success(blogs)
    }
}