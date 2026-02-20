package site.techmoa.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import site.techmoa.application.port.BlogPort
import site.techmoa.domain.exception.ErrorCode
import site.techmoa.domain.exception.NotFoundException
import site.techmoa.domain.model.Blog

class GetBlogUseCaseTest : BehaviorSpec({
    given("블로그 단건 조회에서") {
        val blogPort = mockk<BlogPort>()
        val useCase = GetBlogUseCase(blogPort)
        val blogId = 1L
        val blog = blog(id = blogId, name = "techmoa")

        every { blogPort.findById(blogId) } returns blog

        `when`("존재하는 id로 조회하면") {
            val result = useCase.fetchOne(blogId)

            then("블로그를 반환한다") {
                result shouldBe blog
                verify(exactly = 1) { blogPort.findById(blogId) }
            }
        }
    }

    given("존재하지 않는 블로그 단건 조회에서") {
        val blogPort = mockk<BlogPort>()
        val useCase = GetBlogUseCase(blogPort)
        val missingId = 100L

        every { blogPort.findById(missingId) } returns null

        `when`("없는 id로 조회하면") {
            then("NotFoundException을 던진다") {
                val exception = shouldThrow<NotFoundException> {
                    useCase.fetchOne(missingId)
                }

                exception.errorCode shouldBe ErrorCode.NOT_FOUND_DATA
                exception.message shouldBe "Blog not found with id: 100"
                verify(exactly = 1) { blogPort.findById(missingId) }
            }
        }
    }

    given("여러 블로그 id로 조회에서") {
        val blogPort = mockk<BlogPort>()
        val useCase = GetBlogUseCase(blogPort)
        val ids = listOf(1L, 2L)
        val blogs = listOf(
            blog(id = 1L, name = "a"),
            blog(id = 2L, name = "b")
        )

        every { blogPort.findAllBy(ids) } returns blogs

        `when`("fetchBlogMapByIds를 호출하면") {
            val result = useCase.fetchBlogMapByIds(ids)

            then("id를 key로 하는 맵을 반환한다") {
                result.shouldHaveSize(2)
                result shouldContainKey 1L
                result shouldContainKey 2L
                result[1L] shouldBe blogs[0]
                result[2L] shouldBe blogs[1]
                verify(exactly = 1) { blogPort.findAllBy(ids) }
            }
        }
    }
})

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
