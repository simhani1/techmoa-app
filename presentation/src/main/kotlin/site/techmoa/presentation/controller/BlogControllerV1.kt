package site.techmoa.presentation.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import site.techmoa.application.service.BlogService
import site.techmoa.presentation.common.template.ApiResponse
import site.techmoa.presentation.controller.response.GetAllBlogsResponse

@RequestMapping("/v1/blogs")
@RestController
class BlogControllerV1(
    private val blogService: BlogService,
) {

    @GetMapping
    fun getAllBlogs(): ApiResponse<List<GetAllBlogsResponse>> {
        val response = blogService.getAllBlogs()
            .sortedBy { it.name }
            .mapIndexed { idx, blog ->
                GetAllBlogsResponse(
                    order = idx + 1,
                    name = blog.name,
                    link = blog.link,
                )
            }
        return ApiResponse.success(response)
    }
}