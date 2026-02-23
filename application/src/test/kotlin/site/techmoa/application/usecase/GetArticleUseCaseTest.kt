package site.techmoa.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import site.techmoa.application.common.OffsetLimit
import site.techmoa.application.port.ArticlePort
import site.techmoa.domain.exception.ErrorCode
import site.techmoa.domain.exception.NotFoundException
import site.techmoa.domain.model.Article

class GetArticleUseCaseTest : BehaviorSpec({
    given("게시글 단건 조회에서") {
        val articlePort = mockk<ArticlePort>()
        val useCase = GetArticleUseCase(articlePort)
        val articleId = 10L
        val article = article(id = articleId, pubDate = 1_700_000_000L)

        every { articlePort.findById(articleId) } returns article

        `when`("존재하는 id로 조회하면") {
            val result = useCase.fetchOne(articleId)

            then("게시글을 반환한다") {
                result shouldBe article
                verify(exactly = 1) { articlePort.findById(articleId) }
            }
        }
    }

    given("존재하지 않는 게시글 단건 조회에서") {
        val articlePort = mockk<ArticlePort>()
        val useCase = GetArticleUseCase(articlePort)
        val missingId = 999L

        every { articlePort.findById(missingId) } returns null

        `when`("없는 id로 조회하면") {
            then("NotFoundException을 던진다") {
                val exception = shouldThrow<NotFoundException> {
                    useCase.fetchOne(missingId)
                }

                exception.errorCode shouldBe ErrorCode.NOT_FOUND_DATA
                exception.message shouldBe "Article not found with id: 999"
                verify(exactly = 1) { articlePort.findById(missingId) }
            }
        }
    }

    given("게시글 페이지 조회에서 요청 limit보다 1개 더 조회되면") {
        val articlePort = mockk<ArticlePort>()
        val useCase = GetArticleUseCase(articlePort)
        val cursor = 500L
        val limit = 2
        val fetched = listOf(
            article(id = 1L, pubDate = 300L),
            article(id = 2L, pubDate = 200L),
            article(id = 3L, pubDate = 100L)
        )

        every { articlePort.findPublishedAfter(cursor, OffsetLimit(limit = limit)) } returns fetched

        `when`("fetchPage를 호출하면") {
            val result = useCase.fetchPage(cursor, limit)

            then("limit 개수만 반환하고 다음 커서를 설정한다") {
                result.data shouldBe fetched.take(limit)
                result.hasNext shouldBe true
                result.nextCursor shouldBe 200L
                verify(exactly = 1) {
                    articlePort.findPublishedAfter(cursor, OffsetLimit(limit = limit))
                }
            }
        }
    }

    given("게시글 페이지 조회에서 마지막 페이지라면") {
        val articlePort = mockk<ArticlePort>()
        val useCase = GetArticleUseCase(articlePort)
        val cursor: Long? = null
        val limit = 2
        val fetched = listOf(
            article(id = 1L, pubDate = 300L),
            article(id = 2L, pubDate = 200L)
        )

        every { articlePort.findPublishedAfter(cursor, OffsetLimit(limit = limit)) } returns fetched

        `when`("fetchPage를 호출하면") {
            val result = useCase.fetchPage(cursor, limit)

            then("다음 페이지가 없고 nextCursor는 null이다") {
                result.data shouldBe fetched
                result.hasNext shouldBe false
                result.nextCursor shouldBe null
                verify(exactly = 1) {
                    articlePort.findPublishedAfter(cursor, OffsetLimit(limit = limit))
                }
            }
        }
    }
})

private fun article(
    id: Long,
    pubDate: Long
): Article = Article(
    id = id,
    blogId = 1L,
    title = "title-$id",
    link = "https://example.com/articles/$id",
    guid = "guid-$id",
    pubDate = pubDate,
    views = 0
)
