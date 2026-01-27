package site.techmoa.app.core.blog

import org.springframework.stereotype.Service

@Service
class BlogUseCase(
    private val blogFinder: BlogFinder
){

}