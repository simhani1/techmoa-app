package site.techmoa.app.common.blog

import org.springframework.stereotype.Service

@Service
class BlogUseCase(
    private val blogFinder: BlogFinder
){

}