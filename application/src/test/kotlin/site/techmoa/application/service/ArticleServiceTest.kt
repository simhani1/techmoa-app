package site.techmoa.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import site.techmoa.application.common.Page
import site.techmoa.application.port.ArticlePort
import site.techmoa.application.usecase.GetArticleUseCase
import site.techmoa.application.usecase.GetBlogUseCase
import site.techmoa.domain.model.Article
import site.techmoa.domain.model.Blog

class ArticleServiceTest : BehaviorSpec({

    given("게시글 페이지 조회 요청에서 게시글과 블로그가 모두 존재할 때") {
        val articlePort = mockk<ArticlePort>(relaxed = true)
        val getArticleUseCase = mockk<GetArticleUseCase>()
        val getBlogUseCase = mockk<GetBlogUseCase>()
        val articleService = ArticleService(articlePort, getArticleUseCase, getBlogUseCase)

        val article1 = article(id = 11L, blogId = BLOG_ID_1, pubDate = 200L)
        val article2 = article(id = 12L, blogId = BLOG_ID_2, pubDate = 100L)
        val articlePage = Page(
            data = listOf(article1, article2),
            hasNext = true,
            nextCursor = 100L
        )
        val blogMap = mapOf(
            BLOG_ID_1 to blog(id = BLOG_ID_1, name = "tech-blog-a"),
            BLOG_ID_2 to blog(id = BLOG_ID_2, name = "tech-blog-b")
        )

        every { getArticleUseCase.fetchPage(CURSOR, LIMIT) } returns articlePage
        every { getBlogUseCase.fetchBlogMapByIds(listOf(BLOG_ID_1, BLOG_ID_2)) } returns blogMap

        `when`("fetchPage를 호출하면") {
            val result = articleService.fetchPage(CURSOR, LIMIT)

            then("블로그 정보가 결합된 페이지를 반환한다") {
                result.data.size shouldBe 2
                result.data[0].article shouldBe article1
                result.data[0].blog shouldBe blogMap[BLOG_ID_1]
                result.data[1].article shouldBe article2
                result.data[1].blog shouldBe blogMap[BLOG_ID_2]
                result.hasNext shouldBe true
                result.nextCursor shouldBe 100L

                verify(exactly = 1) { getArticleUseCase.fetchPage(CURSOR, LIMIT) }
                verify(exactly = 1) { getBlogUseCase.fetchBlogMapByIds(listOf(BLOG_ID_1, BLOG_ID_2)) }
            }
        }
    }

    given("게시글 페이지 조회 결과가 비어 있으면") {
        val articlePort = mockk<ArticlePort>(relaxed = true)
        val getArticleUseCase = mockk<GetArticleUseCase>()
        val getBlogUseCase = mockk<GetBlogUseCase>()
        val articleService = ArticleService(articlePort, getArticleUseCase, getBlogUseCase)
        val emptyPage = Page<Article>(
            data = emptyList(),
            hasNext = false,
            nextCursor = null
        )

        every { getArticleUseCase.fetchPage(null, LIMIT) } returns emptyPage
        every { getBlogUseCase.fetchBlogMapByIds(emptyList()) } returns emptyMap()

        `when`("fetchPage를 호출하면") {
            val result = articleService.fetchPage(null, LIMIT)

            then("빈 데이터와 페이지 메타데이터를 그대로 반환한다") {
                result.data.shouldBeEmpty()
                result.hasNext shouldBe false
                result.nextCursor shouldBe null

                verify(exactly = 1) { getArticleUseCase.fetchPage(null, LIMIT) }
                verify(exactly = 1) { getBlogUseCase.fetchBlogMapByIds(emptyList()) }
            }
        }
    }
}) {
    companion object {
        private const val CURSOR = 300L
        private const val LIMIT = 20
        private const val BLOG_ID_1 = 1L
        private const val BLOG_ID_2 = 2L
    }
}

private fun article(
    id: Long,
    blogId: Long,
    pubDate: Long
): Article = Article(
    id = id,
    blogId = blogId,
    title = "title-$id",
    link = "https://example.com/articles/$id",
    guid = "guid-$id",
    pubDate = pubDate,
    views = 0
)

private fun blog(
    id: Long,
    name: String
): Blog = Blog(
    id = id,
    link = "https://$name.example.com",
    name = name,
    logoUrl = "https://$name.example.com/logo.png",
    rssLink = "https://$name.example.com/rss"
)
