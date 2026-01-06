package site.techmoa.app.blog.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import site.techmoa.app.blog.controller.request.SubscribeBlogRequest
import site.techmoa.app.core.response.ApiResponse

@RestController
class AdminBlogController{

    @PostMapping("/v1/admin/blogs")
    fun subscribeBlog(
        @RequestBody request: SubscribeBlogRequest,
    ): ApiResponse<Any> {
        return ApiResponse.success()
    }
}
