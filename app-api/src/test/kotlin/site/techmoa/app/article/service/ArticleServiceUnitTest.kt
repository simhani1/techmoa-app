//package site.techmoa.app.article.service
//
//import io.mockk.impl.annotations.InjectMockKs
//import io.mockk.impl.annotations.MockK
//import site.techmoa.app.ServiceUnitTest
//import site.techmoa.app.article.support.ArticleFinder
//import site.techmoa.app.article.support.ArticleHandler
//import site.techmoa.app.blog.support.BlogFinder
//
//abstract class ArticleServiceUnitTest: ServiceUnitTest() {
//    @InjectMockKs
//    lateinit var articleService: ArticleService
//
//    @MockK
//    lateinit var articleFinder: ArticleFinder
//
//    @MockK
//    lateinit var articleHandler: ArticleHandler
//
//    @MockK
//    lateinit var blogFinder: BlogFinder
//}