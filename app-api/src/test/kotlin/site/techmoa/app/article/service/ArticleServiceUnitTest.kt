package site.techmoa.app.article.service

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import site.techmoa.app.ServiceUnitTest
import site.techmoa.app.article.support.ArticleFinder
import site.techmoa.app.blog.support.BlogFinder
import site.techmoa.app.storage.db.repository.ArticleRepository

abstract class ArticleServiceUnitTest: ServiceUnitTest() {
    @InjectMockKs
    lateinit var articleService: ArticleService

    @MockK
    lateinit var articleFinder: ArticleFinder

    @MockK
    lateinit var blogFinder: BlogFinder

    @MockK
    lateinit var articleRepository: ArticleRepository
}